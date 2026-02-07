package com.paeldav.backend.application.dto.repostaje;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO para la creación de un Repostaje.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepostajeCreateDTO {

    @NotNull(message = "La aeronave es obligatoria")
    private Long aeronaveId;

    private Long vueloId; // Opcional

    @NotNull(message = "La cantidad de combustible es obligatoria")
    @Positive(message = "La cantidad debe ser positiva")
    private Double cantidadLitros;

    @NotBlank(message = "El tipo de combustible es obligatorio")
    private String tipoCombustible;

    private Double nivelAntes;
    private Double nivelDespues;
    private Double precioPorLitro;
    private String proveedor;
    private String numeroFactura;
    private LocalDateTime fechaRepostaje;
    private String ubicacion;
    private Long realizadoPorId;
    private Double horasVueloAeronave;
    private String observaciones;
}
