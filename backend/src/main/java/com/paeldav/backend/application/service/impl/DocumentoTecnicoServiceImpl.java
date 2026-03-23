package com.paeldav.backend.application.service.impl;

import com.paeldav.backend.application.dto.documentotecnico.DocumentoTecnicoCreateDTO;
import com.paeldav.backend.application.dto.documentotecnico.DocumentoTecnicoDTO;
import com.paeldav.backend.application.dto.documentotecnico.DocumentoTecnicoUpdateDTO;
import com.paeldav.backend.application.mapper.DocumentoTecnicoMapper;
import com.paeldav.backend.application.service.base.DocumentoTecnicoService;
import com.paeldav.backend.application.service.integration.CloudinaryService;
import com.paeldav.backend.domain.entity.Aeronave;
import com.paeldav.backend.domain.entity.DocumentoTecnico;
import com.paeldav.backend.domain.entity.Personal;
import com.paeldav.backend.exception.*;
import com.paeldav.backend.infraestructure.repository.AeronaveRepository;
import com.paeldav.backend.infraestructure.repository.DocumentoTecnicoRepository;
import com.paeldav.backend.infraestructure.repository.PersonalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Implementación del servicio de gestión de documentos técnicos.
 * Maneja la carga, almacenamiento y consulta de documentos técnicos de aeronaves.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DocumentoTecnicoServiceImpl implements DocumentoTecnicoService {

    private final DocumentoTecnicoRepository documentoRepository; // Repositorio de documentos
    private final AeronaveRepository aeronaveRepository; // Repositorio de aeronaves
    private final PersonalRepository personalRepository; // Repositorio de personal
    private final DocumentoTecnicoMapper documentoMapper; // Mapper de documentos
    private final CloudinaryService cloudinaryService; // Servicio de Cloudinary para almacenar archivos

    @Override
    public DocumentoTecnicoDTO cargarDocumento(MultipartFile file, DocumentoTecnicoCreateDTO documentoCreateDTO) {
        log.info("Cargando nuevo documento técnico para aeronave ID: {}", documentoCreateDTO.getAeronaveId());

        // Validar que la aeronave exista
        Aeronave aeronave = aeronaveRepository.findById(documentoCreateDTO.getAeronaveId())
                .orElseThrow(() -> {
                    log.warn("Intento de cargar documento para aeronave inexistente ID: {}",
                            documentoCreateDTO.getAeronaveId());
                    return new AeronaveNoEncontradaException(
                            "Aeronave no encontrada con ID: " + documentoCreateDTO.getAeronaveId()
                    );
                });

        // Validar que el archivo no esté vacío
        if (file == null || file.isEmpty()) {
            log.warn("Intento de carga con archivo vacío");
            throw new CargaArchivoException("El archivo está vacío");
        }

        // Validar tamaño máximo (ejemplo: 50MB)
        long maxSize = 50 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            log.warn("Archivo demasiado grande: {} bytes", file.getSize());
            throw new CargaArchivoException("El archivo es demasiado grande (máximo 50MB)");
        }

        // Validar personal si se proporciona
        Personal cargadoPor = null;
        if (documentoCreateDTO.getCargadoPorId() != null) {
            cargadoPor = personalRepository.findById(documentoCreateDTO.getCargadoPorId())
                    .orElseThrow(() -> {
                        log.warn("Personal no encontrado con ID: {}", documentoCreateDTO.getCargadoPorId());
                        return new PersonalNoEncontradoException(
                                "Personal no encontrado con ID: " + documentoCreateDTO.getCargadoPorId()
                        );
                    });
        }

        try {
            // Cargar archivo a Cloudinary
            Map<String, Object> resultadoCarga = cloudinaryService.uploadFile(file, "documentos-tecnicos");
            String urlDocumento = (String) resultadoCarga.get("url");
            String idCloudinary = (String) resultadoCarga.get("public_id");

            // Crear entidad
            DocumentoTecnico documento = documentoMapper.toEntity(documentoCreateDTO);
            documento.setAeronave(aeronave);
            documento.setCargadoPor(cargadoPor);
            documento.setUrlDocumento(urlDocumento);
            documento.setIdCloudinary(idCloudinary);
            documento.setTamañoBytes(file.getSize());
            documento.setTipoArchivo(extraerTipoArchivo(file.getOriginalFilename()));
            documento.setFechaCarga(LocalDateTime.now());

            // Guardar en base de datos
            DocumentoTecnico documentoGuardado = documentoRepository.save(documento);
            log.info("Documento técnico cargado exitosamente con ID: {} para aeronave: {}",
                    documentoGuardado.getId(), aeronave.getMatricula());

            return documentoMapper.toDTO(documentoGuardado);

        } catch (Exception e) {
            log.error("Error al cargar documento técnico: {}", e.getMessage(), e);
            throw new CargaArchivoException("Error al cargar el documento: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentoTecnicoDTO obtenerDocumentoPorId(Long id) {
        log.debug("Buscando documento técnico con ID: {}", id);

        DocumentoTecnico documento = documentoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Documento técnico no encontrado con ID: {}", id);
                    return new DocumentoTecnicoNoEncontradoException("Documento no encontrado con ID: " + id);
                });

        return documentoMapper.toDTO(documento);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentoTecnicoDTO> obtenerTodosDocumentos() {
        log.debug("Obteniendo todos los documentos técnicos");

        List<DocumentoTecnico> documentos = documentoRepository.findAll();
        return documentoMapper.toDTOList(documentos);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentoTecnicoDTO> obtenerDocumentosPorAeronave(Long aeronaveId) {
        log.debug("Obteniendo documentos técnicos para aeronave ID: {}", aeronaveId);

        if (!aeronaveRepository.existsById(aeronaveId)) {
            log.warn("Intento de obtener documentos para aeronave inexistente ID: {}", aeronaveId);
            throw new AeronaveNoEncontradaException("Aeronave no encontrada con ID: " + aeronaveId);
        }

        List<DocumentoTecnico> documentos = documentoRepository.findByAeronaveId(aeronaveId);
        return documentoMapper.toDTOList(documentos);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentoTecnicoDTO> obtenerDocumentosPorTipo(String tipo) {
        log.debug("Obteniendo documentos de tipo: {}", tipo);

        List<DocumentoTecnico> documentos = documentoRepository.findByTipo(tipo);
        return documentoMapper.toDTOList(documentos);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentoTecnicoDTO> obtenerDocumentosPorAeronaveYTipo(Long aeronaveId, String tipo) {
        log.debug("Obteniendo documentos tipo {} para aeronave ID: {}", tipo, aeronaveId);

        if (!aeronaveRepository.existsById(aeronaveId)) {
            log.warn("Intento de obtener documentos para aeronave inexistente ID: {}", aeronaveId);
            throw new AeronaveNoEncontradaException("Aeronave no encontrada con ID: " + aeronaveId);
        }

        List<DocumentoTecnico> documentos = documentoRepository.findByAeronaveIdAndTipo(aeronaveId, tipo);
        return documentoMapper.toDTOList(documentos);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentoTecnicoDTO> obtenerDocumentosPorPersonal(Long personalId) {
        log.debug("Obteniendo documentos cargados por personal ID: {}", personalId);

        if (!personalRepository.existsById(personalId)) {
            log.warn("Personal no encontrado con ID: {}", personalId);
            throw new PersonalNoEncontradoException("Personal no encontrado con ID: " + personalId);
        }

        List<DocumentoTecnico> documentos = documentoRepository.findByCargadoPorId(personalId);
        return documentoMapper.toDTOList(documentos);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentoTecnicoDTO> obtenerUltimosDocumentos(Long aeronaveId) {
        log.debug("Obteniendo últimos documentos para aeronave ID: {}", aeronaveId);

        if (!aeronaveRepository.existsById(aeronaveId)) {
            log.warn("Intento de obtener últimos documentos para aeronave inexistente ID: {}", aeronaveId);
            throw new AeronaveNoEncontradaException("Aeronave no encontrada con ID: " + aeronaveId);
        }

        List<DocumentoTecnico> documentos = documentoRepository.findUltimosDocumentos(aeronaveId);
        return documentoMapper.toDTOList(documentos);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentoTecnicoDTO> obtenerDocumentosVigentes(Long aeronaveId) {
        log.debug("Obteniendo documentos vigentes para aeronave ID: {}", aeronaveId);

        if (!aeronaveRepository.existsById(aeronaveId)) {
            log.warn("Intento de obtener documentos vigentes para aeronave inexistente ID: {}", aeronaveId);
            throw new AeronaveNoEncontradaException("Aeronave no encontrada con ID: " + aeronaveId);
        }

        List<DocumentoTecnico> documentos = documentoRepository.findDocumentosVigentesPorAeronave(aeronaveId);
        return documentoMapper.toDTOList(documentos);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentoTecnicoDTO> obtenerDocumentosVencidos(Long aeronaveId) {
        log.debug("Obteniendo documentos vencidos para aeronave ID: {}", aeronaveId);

        if (!aeronaveRepository.existsById(aeronaveId)) {
            log.warn("Intento de obtener documentos vencidos para aeronave inexistente ID: {}", aeronaveId);
            throw new AeronaveNoEncontradaException("Aeronave no encontrada con ID: " + aeronaveId);
        }

        List<DocumentoTecnico> documentos = documentoRepository.findDocumentosVencidosPorAeronave(aeronaveId);
        return documentoMapper.toDTOList(documentos);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentoTecnicoDTO> obtenerDocumentosProximosAVencer() {
        log.debug("Obteniendo documentos próximos a vencer");

        // Documentos que vencen en los próximos 30 días
        LocalDateTime fecha = LocalDateTime.now().plusDays(30);
        List<DocumentoTecnico> documentos = documentoRepository.findDocumentosProxAVencer(fecha);
        return documentoMapper.toDTOList(documentos);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentoTecnicoDTO> obtenerDocumentosPorFecha(LocalDateTime inicio, LocalDateTime fin) {
        log.debug("Obteniendo documentos cargados entre {} y {}", inicio, fin);

        List<DocumentoTecnico> documentos = documentoRepository.findByFechaCargaBetween(inicio, fin);
        return documentoMapper.toDTOList(documentos);
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentoTecnicoDTO obtenerDocumentoPorNumero(String numeroDocumento) {
        log.debug("Buscando documento con número: {}", numeroDocumento);

        DocumentoTecnico documento = documentoRepository.findByNumeroDocumento(numeroDocumento);
        if (documento == null) {
            log.warn("Documento no encontrado con número: {}", numeroDocumento);
            throw new DocumentoTecnicoNoEncontradoException("Documento no encontrado con número: " + numeroDocumento);
        }

        return documentoMapper.toDTO(documento);
    }

    @Override
    public DocumentoTecnicoDTO actualizarDocumento(Long id, DocumentoTecnicoUpdateDTO documentoUpdateDTO) {
        log.info("Actualizando documento técnico ID: {}", id);

        DocumentoTecnico documento = documentoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Documento técnico no encontrado con ID: {}", id);
                    return new DocumentoTecnicoNoEncontradoException("Documento no encontrado con ID: " + id);
                });

        documentoMapper.updateEntityFromDTO(documentoUpdateDTO, documento);
        DocumentoTecnico documentoActualizado = documentoRepository.save(documento);
        log.info("Documento técnico actualizado exitosamente con ID: {}", id);

        return documentoMapper.toDTO(documentoActualizado);
    }

    @Override
    public void eliminarDocumento(Long id) {
        log.info("Eliminando documento técnico ID: {}", id);

        DocumentoTecnico documento = documentoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Intento de eliminar documento inexistente con ID: {}", id);
                    return new DocumentoTecnicoNoEncontradoException("Documento no encontrado con ID: " + id);
                });

        // Intentar eliminar de Cloudinary si se dispone del idCloudinary
        if (documento.getIdCloudinary() != null && !documento.getIdCloudinary().isEmpty()) {
            try {
                cloudinaryService.deleteFile(documento.getIdCloudinary());
                log.info("Archivo eliminado de Cloudinary: {}", documento.getIdCloudinary());
            } catch (Exception e) {
                log.warn("No se pudo eliminar archivo de Cloudinary: {}", e.getMessage());
                // No lanzar excepción, continuar con la eliminación de BD
            }
        }

        documentoRepository.deleteById(id);
        log.info("Documento técnico eliminado exitosamente con ID: {}", id);
    }

    @Override
    public void marcarNoVigente(Long id) {
        log.info("Marcando como no vigente el documento ID: {}", id);

        DocumentoTecnico documento = documentoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Documento técnico no encontrado con ID: {}", id);
                    return new DocumentoTecnicoNoEncontradoException("Documento no encontrado con ID: " + id);
                });

        documento.setVigente(false);
        documentoRepository.save(documento);
        log.info("Documento marcado como no vigente con ID: {}", id);
    }

    /**
     * Extrae el tipo de archivo de la ruta o nombre del archivo.
     */
    private String extraerTipoArchivo(String filename) {
        if (filename == null || !filename.contains(".")) {
            return null;
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toUpperCase();
    }
}