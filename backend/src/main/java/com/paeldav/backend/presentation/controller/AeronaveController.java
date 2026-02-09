package com.paeldav.backend.presentation.controller;

import com.paeldav.backend.application.dto.aeronave.AeronaveCreateDTO;
import com.paeldav.backend.application.dto.aeronave.AeronaveDTO;
import com.paeldav.backend.application.dto.aeronave.AeronaveUpdateDTO;
import com.paeldav.backend.application.dto.aeronave.HistorialUsoAeronaveDTO;
import com.paeldav.backend.application.dto.aeronave.ImagenAeronaveCreateDTO;
import com.paeldav.backend.application.dto.aeronave.ImagenAeronaveDTO;
import com.paeldav.backend.application.dto.disponibilidad.ResumenDisponibilidadFlotaDTO;
import com.paeldav.backend.application.service.base.AeronaveService;
import com.paeldav.backend.application.service.base.ImagenAeronaveService;
import com.paeldav.backend.domain.enums.EstadoAeronave;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador REST para la gestión integral de aeronaves.
 * Proporciona endpoints para registro, consulta, actualización y eliminación de aeronaves.
 */
@RestController
@RequestMapping("/api/aeronaves")
@RequiredArgsConstructor
public class AeronaveController {

    private final AeronaveService aeronaveService;
    private final ImagenAeronaveService imagenAeronaveService;

    /**
     * Registra una nueva aeronave en el sistema.
     *
     * @param aeronaveCreateDTO DTO con los datos de la nueva aeronave
     * @return ResponseEntity con la aeronave registrada (201 Created)
     */
    @PostMapping
    public ResponseEntity<AeronaveDTO> registrarAeronave(
            @Valid @RequestBody AeronaveCreateDTO aeronaveCreateDTO) {
        AeronaveDTO aeronaveDTO = aeronaveService.registrarAeronave(aeronaveCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(aeronaveDTO);
    }

    /**
     * Obtiene una aeronave por su ID.
     *
     * @param id ID de la aeronave
     * @return ResponseEntity con los datos de la aeronave
     */
    @GetMapping("/{id}")
    public ResponseEntity<AeronaveDTO> obtenerAeronavePorId(@PathVariable Long id) {
        AeronaveDTO aeronaveDTO = aeronaveService.obtenerAeronavePorId(id);
        return ResponseEntity.ok(aeronaveDTO);
    }

    /**
     * Obtiene una aeronave por su matrícula.
     *
     * @param matricula matrícula de la aeronave
     * @return ResponseEntity con los datos de la aeronave
     */
    @GetMapping("/matricula/{matricula}")
    public ResponseEntity<AeronaveDTO> obtenerAeronavePorMatricula(@PathVariable String matricula) {
        AeronaveDTO aeronaveDTO = aeronaveService.obtenerAeronavePorMatricula(matricula);
        return ResponseEntity.ok(aeronaveDTO);
    }

    /**
     * Obtiene todas las aeronaves del sistema.
     *
     * @return ResponseEntity con la lista de todas las aeronaves
     */
    @GetMapping
    public ResponseEntity<List<AeronaveDTO>> obtenerTodasAeronaves() {
        List<AeronaveDTO> aeronaves = aeronaveService.obtenerTodasAeronaves();
        return ResponseEntity.ok(aeronaves);
    }

    /**
     * Obtiene aeronaves filtradas por estado operativo.
     *
     * @param estado estado a filtrar
     * @return ResponseEntity con la lista de aeronaves en el estado especificado
     */
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<AeronaveDTO>> obtenerAerronavesPorEstado(
            @PathVariable EstadoAeronave estado) {
        List<AeronaveDTO> aeronaves = aeronaveService.obtenerAerronavesPorEstado(estado);
        return ResponseEntity.ok(aeronaves);
    }

    /**
     * Obtiene aeronaves filtradas por modelo.
     *
     * @param modelo modelo de aeronave
     * @return ResponseEntity con la lista de aeronaves del modelo especificado
     */
    @GetMapping("/modelo/{modelo}")
    public ResponseEntity<List<AeronaveDTO>> obtenerAerronavesPorModelo(
            @PathVariable String modelo) {
        List<AeronaveDTO> aeronaves = aeronaveService.obtenerAerronavesPorModelo(modelo);
        return ResponseEntity.ok(aeronaves);
    }

    /**
     * Obtiene aeronaves que cumplen con una capacidad mínima de pasajeros.
     *
     * @param capacidad capacidad mínima de pasajeros
     * @return ResponseEntity con la lista de aeronaves con la capacidad especificada
     */
    @GetMapping("/capacidad/{capacidad}")
    public ResponseEntity<List<AeronaveDTO>> obtenerAerronavesPorCapacidad(
            @PathVariable Integer capacidad) {
        List<AeronaveDTO> aeronaves = aeronaveService.obtenerAerronavesPorCapacidad(capacidad);
        return ResponseEntity.ok(aeronaves);
    }

    /**
     * Actualiza la información técnica de una aeronave existente.
     *
     * @param id ID de la aeronave a actualizar
     * @param aeronaveUpdateDTO DTO con los datos a actualizar
     * @return ResponseEntity con los datos de la aeronave actualizada
     */
    @PutMapping("/{id}")
    public ResponseEntity<AeronaveDTO> actualizarAeronave(
            @PathVariable Long id,
            @Valid @RequestBody AeronaveUpdateDTO aeronaveUpdateDTO) {
        AeronaveDTO aeronaveDTO = aeronaveService.actualizarAeronave(id, aeronaveUpdateDTO);
        return ResponseEntity.ok(aeronaveDTO);
    }

    /**
     * Cambia el estado operativo de una aeronave.
     *
     * @param id ID de la aeronave
     * @param nuevoEstado nuevo estado operativo
     * @return ResponseEntity con los datos de la aeronave con el estado actualizado
     */
    @PatchMapping("/{id}/estado")
    public ResponseEntity<AeronaveDTO> cambiarEstadoAeronave(
            @PathVariable Long id,
            @RequestParam EstadoAeronave nuevoEstado) {
        AeronaveDTO aeronaveDTO = aeronaveService.cambiarEstadoAeronave(id, nuevoEstado);
        return ResponseEntity.ok(aeronaveDTO);
    }

    /**
     * Elimina una aeronave del sistema.
     *
     * @param id ID de la aeronave a eliminar
     * @return ResponseEntity sin contenido (204 No Content)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarAeronave(@PathVariable Long id) {
        aeronaveService.eliminarAeronave(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Valida la capacidad operativa de una aeronave.
     * Lanza excepción si la capacidad es insuficiente.
     *
     * @param id ID de la aeronave
     * @param numeroPasajeros número de pasajeros
     * @param numeroTripulantes número de tripulantes
     * @return ResponseEntity con estado 200 OK si la validación es exitosa
     */
    @GetMapping("/{id}/validar-capacidad")
    public ResponseEntity<Void> validarCapacidadOperativa(
            @PathVariable Long id,
            @RequestParam Integer numeroPasajeros,
            @RequestParam Integer numeroTripulantes) {
        aeronaveService.validarCapacidadOperativa(id, numeroPasajeros, numeroTripulantes);
        return ResponseEntity.ok().build();
    }

    /**
     * Incrementa las horas de vuelo de una aeronave.
     *
     * @param id ID de la aeronave
     * @param horasVuelo horas de vuelo a agregar
     * @return ResponseEntity sin contenido (204 No Content)
     */
    @PostMapping("/{id}/incrementar-horas")
    public ResponseEntity<Void> incrementarHorasVuelo(
            @PathVariable Long id,
            @RequestParam Double horasVuelo) {
        aeronaveService.incrementarHorasVuelo(id, horasVuelo);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtiene el resumen de disponibilidad de toda la flota.
     *
     * @return ResponseEntity con el resumen de disponibilidad
     */
    @GetMapping("/disponibilidad/resumen")
    public ResponseEntity<ResumenDisponibilidadFlotaDTO> obtenerResumenDisponibilidadFlota() {
        ResumenDisponibilidadFlotaDTO resumen = aeronaveService.consultarResumenDisponibilidadFlota();
        return ResponseEntity.ok(resumen);
    }

    /**
     * Bloquea una aeronave, cambiándola a estado FUERA_DE_SERVICIO.
     *
     * @param id ID de la aeronave a bloquear
     * @param motivo motivo del bloqueo (opcional)
     * @return ResponseEntity con los datos de la aeronave bloqueada
     */
    @PostMapping("/{id}/bloquear")
    public ResponseEntity<AeronaveDTO> bloquearAeronave(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "Bloqueo manual") String motivo) {
        AeronaveDTO aeronaveDTO = aeronaveService.bloquearAeronave(id, motivo);
        return ResponseEntity.ok(aeronaveDTO);
    }

    /**
     * Desbloquea una aeronave, cambiándola de FUERA_DE_SERVICIO a DISPONIBLE.
     *
     * @param id ID de la aeronave a desbloquear
     * @return ResponseEntity con los datos de la aeronave desbloqueada
     */
    @PostMapping("/{id}/desbloquear")
    public ResponseEntity<AeronaveDTO> desbloquearAeronave(@PathVariable Long id) {
        AeronaveDTO aeronaveDTO = aeronaveService.desbloquearAeronave(id);
        return ResponseEntity.ok(aeronaveDTO);
    }

    /**
     * Obtiene el historial de uso completo de una aeronave.
     * Incluye vuelos, mantenimientos, repostajes y estadísticas.
     *
     * @param id ID de la aeronave
     * @param fechaDesde fecha de inicio del período (opcional)
     * @param fechaHasta fecha de fin del período (opcional)
     * @return ResponseEntity con el historial de uso de la aeronave
     */
    @GetMapping("/{id}/historial-uso")
    public ResponseEntity<HistorialUsoAeronaveDTO> obtenerHistorialUso(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHasta) {
        
        HistorialUsoAeronaveDTO historial;
        if (fechaDesde != null && fechaHasta != null) {
            historial = aeronaveService.obtenerHistorialUso(id, fechaDesde, fechaHasta);
        } else {
            historial = aeronaveService.obtenerHistorialUso(id);
        }
        return ResponseEntity.ok(historial);
    }

    /**
     * Carga una imagen para una aeronave.
     *
     * @param aeronaveId ID de la aeronave
     * @param file archivo de imagen a cargar
     * @param tipo tipo de imagen (EXTERIOR, INTERIOR, CABINA, etc.)
     * @param descripcion descripción de la imagen (opcional)
     * @param orden orden de visualización (opcional)
     * @return ResponseEntity con los datos de la imagen cargada (201 Created)
     */
    @PostMapping("/{aeronaveId}/imagenes")
    public ResponseEntity<ImagenAeronaveDTO> cargarImagen(
            @PathVariable Long aeronaveId,
            @RequestPart("file") MultipartFile file,
            @RequestParam String tipo,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) Integer orden) {

        ImagenAeronaveCreateDTO imagenDTO = ImagenAeronaveCreateDTO.builder()
                .tipo(com.paeldav.backend.domain.enums.TipoImagenAeronave.valueOf(tipo))
                .descripcion(descripcion)
                .ordenVisualizacion(orden)
                .build();

        ImagenAeronaveDTO imagenCargada = imagenAeronaveService.cargarImagen(aeronaveId, file, imagenDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(imagenCargada);
    }

    /**
     * Carga múltiples imágenes para una aeronave.
     *
     * @param aeronaveId ID de la aeronave
     * @param files lista de archivos de imagen a cargar
     * @param tipo tipo de imagen (EXTERIOR, INTERIOR, CABINA, etc.)
     * @param descripcion descripción de las imágenes (opcional)
     * @return ResponseEntity con la lista de imágenes cargadas (201 Created)
     */
    @PostMapping("/{aeronaveId}/imagenes-multiples")
    public ResponseEntity<List<ImagenAeronaveDTO>> cargarMultiplesImagenes(
            @PathVariable Long aeronaveId,
            @RequestPart("files") List<MultipartFile> files,
            @RequestParam String tipo,
            @RequestParam(required = false) String descripcion) {

        ImagenAeronaveCreateDTO imagenDTO = ImagenAeronaveCreateDTO.builder()
                .tipo(com.paeldav.backend.domain.enums.TipoImagenAeronave.valueOf(tipo))
                .descripcion(descripcion)
                .build();

        List<ImagenAeronaveDTO> imagenesConCargadas = imagenAeronaveService.cargarMultiplesImagenes(aeronaveId, files, imagenDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(imagenesConCargadas);
    }

    /**
     * Obtiene todas las imágenes de una aeronave.
     *
     * @param aeronaveId ID de la aeronave
     * @return ResponseEntity con la lista de imágenes de la aeronave
     */
    @GetMapping("/{aeronaveId}/imagenes")
    public ResponseEntity<List<ImagenAeronaveDTO>> obtenerImagenesAeronave(@PathVariable Long aeronaveId) {
        List<ImagenAeronaveDTO> imagenes = imagenAeronaveService.obtenerImagenesAeronave(aeronaveId);
        return ResponseEntity.ok(imagenes);
    }

    /**
     * Obtiene las imágenes de una aeronave filtradas por tipo.
     *
     * @param aeronaveId ID de la aeronave
     * @param tipo tipo de imagen a filtrar
     * @return ResponseEntity con la lista de imágenes del tipo especificado
     */
    @GetMapping("/{aeronaveId}/imagenes/tipo/{tipo}")
    public ResponseEntity<List<ImagenAeronaveDTO>> obtenerImagenesPorTipo(
            @PathVariable Long aeronaveId,
            @PathVariable String tipo) {
        List<ImagenAeronaveDTO> imagenes = imagenAeronaveService.obtenerImagenesPorTipo(
                aeronaveId,
                com.paeldav.backend.domain.enums.TipoImagenAeronave.valueOf(tipo)
        );
        return ResponseEntity.ok(imagenes);
    }

    /**
     * Obtiene una imagen específica.
     *
     * @param imagenId ID de la imagen
     * @return ResponseEntity con los datos de la imagen
     */
    @GetMapping("/imagenes/{imagenId}")
    public ResponseEntity<ImagenAeronaveDTO> obtenerImagen(@PathVariable Long imagenId) {
        ImagenAeronaveDTO imagen = imagenAeronaveService.obtenerImagen(imagenId);
        return ResponseEntity.ok(imagen);
    }

    /**
     * Elimina una imagen de una aeronave.
     *
     * @param aeronaveId ID de la aeronave
     * @param imagenId ID de la imagen a eliminar
     * @return ResponseEntity sin contenido (204 No Content)
     */
    @DeleteMapping("/{aeronaveId}/imagenes/{imagenId}")
    public ResponseEntity<Void> eliminarImagen(
            @PathVariable Long aeronaveId,
            @PathVariable Long imagenId) {
        imagenAeronaveService.eliminarImagen(aeronaveId, imagenId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Actualiza el orden de visualización de las imágenes de una aeronave.
     *
     * @param aeronaveId ID de la aeronave
     * @param ordenUpdates mapa con imagenId -> nuevoOrden
     * @return ResponseEntity sin contenido (204 No Content)
     */
    @PutMapping("/{aeronaveId}/imagenes/orden")
    public ResponseEntity<Void> actualizarOrdenImagenes(
            @PathVariable Long aeronaveId,
            @RequestBody java.util.Map<Long, Integer> ordenUpdates) {
        imagenAeronaveService.actualizarOrdenImagenes(aeronaveId, ordenUpdates);
        return ResponseEntity.noContent().build();
    }

    /**
     * Reordena las imágenes automáticamente basado en su fecha de carga.
     *
     * @param aeronaveId ID de la aeronave
     * @return ResponseEntity sin contenido (204 No Content)
     */
    @PostMapping("/{aeronaveId}/imagenes/reordenar")
    public ResponseEntity<Void> reordenarImagenesAutomatico(@PathVariable Long aeronaveId) {
        imagenAeronaveService.reordenarImagenesAutomatico(aeronaveId);
        return ResponseEntity.noContent().build();
    }
}
