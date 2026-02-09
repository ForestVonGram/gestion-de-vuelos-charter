package com.paeldav.backend.application.service.base;

import com.paeldav.backend.application.dto.documentotecnico.DocumentoTecnicoCreateDTO;
import com.paeldav.backend.application.dto.documentotecnico.DocumentoTecnicoDTO;
import com.paeldav.backend.application.dto.documentotecnico.DocumentoTecnicoUpdateDTO;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Interfaz de servicio para la gestión de documentos técnicos.
 * Define operaciones de CRUD y consultas especializadas para documentos técnicos de aeronaves.
 */
public interface DocumentoTecnicoService {

    /**
     * Carga un nuevo documento técnico a Cloudinary y lo registra en la BD.
     *
     * @param file archivo a cargar
     * @param documentoCreateDTO datos del documento
     * @return DTO del documento registrado
     */
    DocumentoTecnicoDTO cargarDocumento(MultipartFile file, DocumentoTecnicoCreateDTO documentoCreateDTO);

    /**
     * Obtiene un documento técnico por su ID.
     *
     * @param id ID del documento
     * @return DTO del documento
     */
    DocumentoTecnicoDTO obtenerDocumentoPorId(Long id);

    /**
     * Obtiene todos los documentos técnicos del sistema.
     *
     * @return Lista de DTOs de documentos
     */
    List<DocumentoTecnicoDTO> obtenerTodosDocumentos();

    /**
     * Obtiene todos los documentos de una aeronave específica.
     *
     * @param aeronaveId ID de la aeronave
     * @return Lista de DTOs de documentos
     */
    List<DocumentoTecnicoDTO> obtenerDocumentosPorAeronave(Long aeronaveId);

    /**
     * Obtiene los documentos de un tipo específico.
     *
     * @param tipo tipo de documento
     * @return Lista de DTOs de documentos
     */
    List<DocumentoTecnicoDTO> obtenerDocumentosPorTipo(String tipo);

    /**
     * Obtiene los documentos de una aeronave de un tipo específico.
     *
     * @param aeronaveId ID de la aeronave
     * @param tipo tipo de documento
     * @return Lista de DTOs de documentos
     */
    List<DocumentoTecnicoDTO> obtenerDocumentosPorAeronaveYTipo(Long aeronaveId, String tipo);

    /**
     * Obtiene los documentos cargados por un personal específico.
     *
     * @param personalId ID del personal
     * @return Lista de DTOs de documentos
     */
    List<DocumentoTecnicoDTO> obtenerDocumentosPorPersonal(Long personalId);

    /**
     * Obtiene los últimos documentos cargados para una aeronave.
     *
     * @param aeronaveId ID de la aeronave
     * @return Lista de DTOs de documentos
     */
    List<DocumentoTecnicoDTO> obtenerUltimosDocumentos(Long aeronaveId);

    /**
     * Obtiene los documentos vigentes de una aeronave.
     *
     * @param aeronaveId ID de la aeronave
     * @return Lista de DTOs de documentos vigentes
     */
    List<DocumentoTecnicoDTO> obtenerDocumentosVigentes(Long aeronaveId);

    /**
     * Obtiene los documentos vencidos de una aeronave.
     *
     * @param aeronaveId ID de la aeronave
     * @return Lista de DTOs de documentos vencidos
     */
    List<DocumentoTecnicoDTO> obtenerDocumentosVencidos(Long aeronaveId);

    /**
     * Obtiene los documentos próximos a vencer (en los próximos 30 días).
     *
     * @return Lista de DTOs de documentos próximos a vencer
     */
    List<DocumentoTecnicoDTO> obtenerDocumentosProximosAVencer();

    /**
     * Obtiene los documentos cargados en un rango de fechas.
     *
     * @param inicio fecha de inicio
     * @param fin fecha de fin
     * @return Lista de DTOs de documentos
     */
    List<DocumentoTecnicoDTO> obtenerDocumentosPorFecha(LocalDateTime inicio, LocalDateTime fin);

    /**
     * Obtiene un documento por su número de documento.
     *
     * @param numeroDocumento número de documento
     * @return DTO del documento
     */
    DocumentoTecnicoDTO obtenerDocumentoPorNumero(String numeroDocumento);

    /**
     * Actualiza los metadatos de un documento.
     *
     * @param id ID del documento
     * @param documentoUpdateDTO datos a actualizar
     * @return DTO del documento actualizado
     */
    DocumentoTecnicoDTO actualizarDocumento(Long id, DocumentoTecnicoUpdateDTO documentoUpdateDTO);

    /**
     * Elimina un documento técnico (también elimina de Cloudinary si es posible).
     *
     * @param id ID del documento a eliminar
     */
    void eliminarDocumento(Long id);

    /**
     * Marca un documento como no vigente.
     *
     * @param id ID del documento
     */
    void marcarNoVigente(Long id);
}
