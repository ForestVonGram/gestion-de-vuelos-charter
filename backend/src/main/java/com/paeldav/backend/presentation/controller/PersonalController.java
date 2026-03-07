package com.paeldav.backend.presentation.controller;

import com.paeldav.backend.application.dto.personal.PersonalCreateDTO;
import com.paeldav.backend.application.dto.personal.PersonalDTO;
import com.paeldav.backend.application.dto.personal.PersonalUpdateDTO;
import com.paeldav.backend.application.service.base.PersonalService;
import com.paeldav.backend.domain.enums.CargoPersonal;
import com.paeldav.backend.domain.enums.EstadoPersonal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de personal.
 */
@RestController
@RequestMapping("/api/personal")
@RequiredArgsConstructor
public class PersonalController {

    private final PersonalService personalService;

    /**
     * Registra nuevo personal en el sistema.
     */
    @PostMapping("/registrar")
    public ResponseEntity<PersonalDTO> registrarPersonal(
            @Valid @RequestBody PersonalCreateDTO personalCreateDTO) {
        PersonalDTO personal = personalService.registrarPersonal(personalCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(personal);
    }

    /**
     * Obtiene personal por su ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PersonalDTO> obtenerPersonal(@PathVariable Long id) {
        PersonalDTO personal = personalService.obtenerPersonalPorId(id);
        return ResponseEntity.ok(personal);
    }

    /**
     * Obtiene personal por número de empleado.
     */
    @GetMapping("/empleado/{numeroEmpleado}")
    public ResponseEntity<PersonalDTO> obtenerPersonalPorNumeroEmpleado(
            @PathVariable String numeroEmpleado) {
        PersonalDTO personal = personalService.obtenerPersonalPorNumeroEmpleado(numeroEmpleado);
        return ResponseEntity.ok(personal);
    }

    /**
     * Obtiene todo el personal registrado.
     */
    @GetMapping
    public ResponseEntity<List<PersonalDTO>> obtenerTodoPersonal() {
        List<PersonalDTO> personal = personalService.obtenerTodoPersonal();
        return ResponseEntity.ok(personal);
    }

    @GetMapping("/filtros")
    public ResponseEntity<List<PersonalDTO>> filtrarPersonal(@RequestParam(required = false) String nombre,
                                                             @RequestParam (required = false) EstadoPersonal estadoPersonal,
                                                             @RequestParam(required = false) CargoPersonal cargo) {
        List<PersonalDTO> pesonal = personalService.filtrarPersonal(nombre, cargo,estadoPersonal);
        System.out.println(">>> Llegó a /filtros");
        System.out.println(">>> nombre: " + nombre);
        System.out.println(">>> cargo: " + cargo);
        System.out.println(">>> estado: " + estadoPersonal);
        return ResponseEntity.ok(pesonal);
    }

    /**
     * Edita la información de personal.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PersonalDTO> editarPersonal(
            @PathVariable Long id,
            @Valid @RequestBody PersonalUpdateDTO personalUpdateDTO) {
        PersonalDTO personal = personalService.editarPersonal(id, personalUpdateDTO);
        return ResponseEntity.ok(personal);
    }

    /**
     * Elimina personal del sistema.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPersonal(@PathVariable Long id) {
        personalService.eliminarPersonal(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Desactiva personal en el sistema.
     */
    @PutMapping("/{id}/desactivar")
    public ResponseEntity<String> desactivarPersonal(@PathVariable Long id) {
        personalService.desactivarPersonal(id);
        return ResponseEntity.ok("Personal desactivado exitosamente");
    }

    /**
     * Activa personal desactivado.
     */
    @PutMapping("/{id}/activar")
    public ResponseEntity<String> activarPersonal(@PathVariable Long id) {
        personalService.activarPersonal(id);
        return ResponseEntity.ok("Personal activado exitosamente");
    }
}
