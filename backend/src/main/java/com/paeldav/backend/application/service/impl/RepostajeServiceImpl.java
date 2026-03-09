package com.paeldav.backend.application.service.impl;

import com.paeldav.backend.application.dto.repostaje.RepostajeCreateDTO;
import com.paeldav.backend.application.dto.repostaje.RepostajeDTO;
import com.paeldav.backend.application.mapper.RepostajeMapper;
import com.paeldav.backend.application.service.base.RepostajeService;
import com.paeldav.backend.domain.entity.Aeronave;
import com.paeldav.backend.domain.entity.Personal;
import com.paeldav.backend.domain.entity.Repostaje;
import com.paeldav.backend.domain.entity.Vuelo;
import com.paeldav.backend.exception.AeronaveNoEncontradaException;
import com.paeldav.backend.exception.PersonalNoEncontradoException;
import com.paeldav.backend.exception.RepostajeNoEncontradoException;
import com.paeldav.backend.exception.VueloNoEncontradoException;
import com.paeldav.backend.infraestructure.repository.AeronaveRepository;
import com.paeldav.backend.infraestructure.repository.PersonalRepository;
import com.paeldav.backend.infraestructure.repository.RepostajeRepository;
import com.paeldav.backend.infraestructure.repository.VueloRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementación del servicio de gestión de repostajes.
 * Maneja el registro, consulta y análisis de repostajes de combustible.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RepostajeServiceImpl implements RepostajeService {

    // Dependencias inyectadas para interactuar con las entidades involucradas en un repostaje
    private final RepostajeRepository repostajeRepository;
    private final AeronaveRepository aeronaveRepository;
    private final VueloRepository vueloRepository;
    private final PersonalRepository personalRepository;
    private final RepostajeMapper repostajeMapper;

    @Override
    public RepostajeDTO registrarRepostaje(RepostajeCreateDTO repostajeCreateDTO) {
        log.info("Registrando nuevo repostaje para aeronave ID: {}", repostajeCreateDTO.getAeronaveId());

        // Validar que la aeronave objetivo del repostaje exista en el sistema
        Aeronave aeronave = aeronaveRepository.findById(repostajeCreateDTO.getAeronaveId())
                .orElseThrow(() -> {
                    log.warn("Intento de registrar repostaje para aeronave inexistente ID: {}",
                            repostajeCreateDTO.getAeronaveId());
                    return new AeronaveNoEncontradaException(
                            "Aeronave no encontrada con ID: " + repostajeCreateDTO.getAeronaveId()
                    );
                });

        // Validar la existencia del vuelo asociado, si es que el repostaje está vinculado a uno
        Vuelo vuelo = null;
        if (repostajeCreateDTO.getVueloId() != null) {
            vuelo = vueloRepository.findById(repostajeCreateDTO.getVueloId())
                    .orElseThrow(() -> {
                        log.warn("Intento de registrar repostaje para vuelo inexistente ID: {}",
                                repostajeCreateDTO.getVueloId());
                        return new VueloNoEncontradoException(
                                "Vuelo no encontrado con ID: " + repostajeCreateDTO.getVueloId()
                        );
                    });
        }

        // Validar la existencia del miembro del personal que realiza la operación, si se especificó
        Personal realizadoPor = null;
        if (repostajeCreateDTO.getRealizadoPorId() != null) {
            realizadoPor = personalRepository.findById(repostajeCreateDTO.getRealizadoPorId())
                    .orElseThrow(() -> {
                        log.warn("Personal no encontrado con ID: {}", repostajeCreateDTO.getRealizadoPorId());
                        return new PersonalNoEncontradoException(
                                "Personal no encontrado con ID: " + repostajeCreateDTO.getRealizadoPorId()
                        );
                    });
        }

        // Mapear los datos de entrada a la entidad y asignar sus relaciones
        Repostaje repostaje = repostajeMapper.toEntity(repostajeCreateDTO);
        repostaje.setAeronave(aeronave);
        repostaje.setVuelo(vuelo);
        repostaje.setRealizadoPor(realizadoPor);

        // Establecer la fecha actual como momento del repostaje si no se proveyó una fecha explícita
        if (repostaje.getFechaRepostaje() == null) {
            repostaje.setFechaRepostaje(LocalDateTime.now());
        }

        // Persistir el registro y retornarlo al cliente en formato DTO
        Repostaje repostajeGuardado = repostajeRepository.save(repostaje);
        log.info("Repostaje registrado exitosamente con ID: {} para aeronave: {}",
                repostajeGuardado.getId(), aeronave.getMatricula());

        return repostajeMapper.toDTO(repostajeGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public RepostajeDTO obtenerRepostajePorId(Long id) {
        log.debug("Buscando repostaje con ID: {}", id);

        // Buscar el repostaje o lanzar excepción si no existe
        Repostaje repostaje = repostajeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Repostaje no encontrado con ID: {}", id);
                    return new RepostajeNoEncontradoException("Repostaje no encontrado con ID: " + id);
                });

        return repostajeMapper.toDTO(repostaje);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RepostajeDTO> obtenerTodosRepostajes() {
        log.debug("Obteniendo todos los repostajes");

        // Retornar el historial completo de repostajes
        List<Repostaje> repostajes = repostajeRepository.findAll();
        return repostajeMapper.toDTOList(repostajes);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RepostajeDTO> obtenerRepostajePorAeronave(Long aeronaveId) {
        log.debug("Obteniendo repostajes para aeronave ID: {}", aeronaveId);

        // Validar existencia de la aeronave antes de realizar la consulta
        if (!aeronaveRepository.existsById(aeronaveId)) {
            log.warn("Intento de obtener repostajes para aeronave inexistente ID: {}", aeronaveId);
            throw new AeronaveNoEncontradaException("Aeronave no encontrada con ID: " + aeronaveId);
        }

        // Retornar los repostajes de esa aeronave específica
        List<Repostaje> repostajes = repostajeRepository.findByAeronaveId(aeronaveId);
        return repostajeMapper.toDTOList(repostajes);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RepostajeDTO> obtenerRepostajePorVuelo(Long vueloId) {
        log.debug("Obteniendo repostajes para vuelo ID: {}", vueloId);

        // Validar existencia del vuelo antes de proceder
        if (!vueloRepository.existsById(vueloId)) {
            log.warn("Intento de obtener repostajes para vuelo inexistente ID: {}", vueloId);
            throw new VueloNoEncontradoException("Vuelo no encontrado con ID: " + vueloId);
        }

        // Retornar repostajes asociados a un vuelo en particular
        List<Repostaje> repostajes = repostajeRepository.findByVueloId(vueloId);
        return repostajeMapper.toDTOList(repostajes);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RepostajeDTO> obtenerRepostajePorFecha(LocalDateTime inicio, LocalDateTime fin) {
        log.debug("Obteniendo repostajes entre {} y {}", inicio, fin);

        // Consultar los repostajes realizados en un rango de tiempo determinado
        List<Repostaje> repostajes = repostajeRepository.findByFechaBetween(inicio, fin);
        return repostajeMapper.toDTOList(repostajes);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RepostajeDTO> obtenerRepostajePorPersonal(Long personalId) {
        log.debug("Obteniendo repostajes realizados por personal ID: {}", personalId);

        // Validar existencia del miembro del personal
        if (!personalRepository.existsById(personalId)) {
            log.warn("Personal no encontrado con ID: {}", personalId);
            throw new PersonalNoEncontradoException("Personal no encontrado con ID: " + personalId);
        }

        // Retornar los repostajes efectuados por un empleado específico
        List<Repostaje> repostajes = repostajeRepository.findByRealizadoPorId(personalId);
        return repostajeMapper.toDTOList(repostajes);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RepostajeDTO> obtenerUltimosRepostajes(Long aeronaveId) {
        log.debug("Obteniendo últimos repostajes para aeronave ID: {}", aeronaveId);

        // Validar existencia de la aeronave
        if (!aeronaveRepository.existsById(aeronaveId)) {
            log.warn("Intento de obtener últimos repostajes para aeronave inexistente ID: {}", aeronaveId);
            throw new AeronaveNoEncontradaException("Aeronave no encontrada con ID: " + aeronaveId);
        }

        // Devolver los registros más recientes para una aeronave dada
        List<Repostaje> repostajes = repostajeRepository.findUltimosRepostajes(aeronaveId);
        return repostajeMapper.toDTOList(repostajes);
    }

    @Override
    @Transactional(readOnly = true)
    public Double calcularCombustibleTotalAeronave(Long aeronaveId) {
        log.debug("Calculando combustible total para aeronave ID: {}", aeronaveId);

        // Validar existencia de la aeronave
        if (!aeronaveRepository.existsById(aeronaveId)) {
            log.warn("Intento de calcular combustible para aeronave inexistente ID: {}", aeronaveId);
            throw new AeronaveNoEncontradaException("Aeronave no encontrada con ID: " + aeronaveId);
        }

        // Ejecutar suma de combustible desde la base de datos y manejar posibles valores nulos
        Double total = repostajeRepository.sumCantidadByAeronaveId(aeronaveId);
        return total != null ? total : 0.0;
    }

    @Override
    @Transactional(readOnly = true)
    public Double calcularCostoTotalPeriodo(LocalDateTime inicio, LocalDateTime fin) {
        log.debug("Calculando costo total de repostajes entre {} y {}", inicio, fin);

        // Sumarizar los costos de combustible en un periodo específico de tiempo
        Double total = repostajeRepository.sumCostoTotalByFechaBetween(inicio, fin);
        return total != null ? total : 0.0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RepostajeDTO> obtenerRepostajePorProveedor(String proveedor) {
        log.debug("Obteniendo repostajes del proveedor: {}", proveedor);

        // Filtrar el historial por el nombre de la compañía proveedora
        List<Repostaje> repostajes = repostajeRepository.findByProveedor(proveedor);
        return repostajeMapper.toDTOList(repostajes);
    }

    @Override
    public RepostajeDTO actualizarRepostaje(Long id, RepostajeCreateDTO repostajeCreateDTO) {
        log.info("Actualizando repostaje ID: {}", id);

        // Recuperar el registro existente a modificar
        Repostaje repostaje = repostajeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Repostaje no encontrado con ID: {}", id);
                    return new RepostajeNoEncontradoException("Repostaje no encontrado con ID: " + id);
                });

        // Si se indicó una aeronave distinta en el DTO, validarla y actualizar la relación
        if (!repostaje.getAeronave().getId().equals(repostajeCreateDTO.getAeronaveId())) {
            Aeronave aeronave = aeronaveRepository.findById(repostajeCreateDTO.getAeronaveId())
                    .orElseThrow(() -> new AeronaveNoEncontradaException(
                            "Aeronave no encontrada con ID: " + repostajeCreateDTO.getAeronaveId()
                    ));
            repostaje.setAeronave(aeronave);
        }

        // Si se indicó un vuelo distinto en el DTO, validarlo y actualizar la relación
        if (repostajeCreateDTO.getVueloId() != null && !repostajeCreateDTO.getVueloId()
                .equals(repostaje.getVuelo() != null ? repostaje.getVuelo().getId() : null)) {
            Vuelo vuelo = vueloRepository.findById(repostajeCreateDTO.getVueloId())
                    .orElseThrow(() -> new VueloNoEncontradoException(
                            "Vuelo no encontrado con ID: " + repostajeCreateDTO.getVueloId()
                    ));
            repostaje.setVuelo(vuelo);
        }

        // Aplicar los cambios de los campos simples desde el DTO hacia la entidad
        repostajeMapper.updateEntityFromDTO(repostajeCreateDTO, repostaje);

        // Guardar la entidad actualizada y retornarla
        Repostaje repostajeActualizado = repostajeRepository.save(repostaje);
        log.info("Repostaje actualizado exitosamente con ID: {}", id);

        return repostajeMapper.toDTO(repostajeActualizado);
    }

    @Override
    public void eliminarRepostaje(Long id) {
        log.info("Eliminando repostaje ID: {}", id);

        // Verificar existencia antes de intentar borrar
        if (!repostajeRepository.existsById(id)) {
            log.warn("Intento de eliminar repostaje inexistente con ID: {}", id);
            throw new RepostajeNoEncontradoException("Repostaje no encontrado con ID: " + id);
        }

        // Eliminar físicamente el registro de la base de datos
        repostajeRepository.deleteById(id);
        log.info("Repostaje eliminado exitosamente con ID: {}", id);
    }
}