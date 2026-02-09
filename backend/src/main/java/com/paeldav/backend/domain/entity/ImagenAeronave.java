package com.paeldav.backend.domain.entity;

import com.paeldav.backend.domain.enums.TipoImagenAeronave;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad que representa una imagen de una aeronave.
 * Almacena referencias a imágenes alojadas en Cloudinary para mostrar
 * características interiores y exteriores de la aeronave.
 */
@Entity
@Table(name = "imagenes_aeronaves")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImagenAeronave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La aeronave es obligatoria")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aeronave_id", nullable = false)
    private Aeronave aeronave;

    @NotBlank(message = "La URL de la imagen es obligatoria")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String urlImagen;

    @NotBlank(message = "El ID de Cloudinary es obligatorio")
    @Column(name = "id_cloudinary", nullable = false)
    private String idCloudinary;

    @NotNull(message = "El tipo de imagen es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoImagenAeronave tipo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "orden_visualizacion")
    private Integer ordenVisualizacion;

    @NotNull(message = "La fecha de carga es obligatoria")
    @Column(nullable = false)
    private LocalDateTime fechaCarga;

    @Column(name = "tamaño_bytes")
    private Long tamanoBytes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cargado_por_id")
    private Personal cargadoPor;

    @PrePersist
    protected void onCreate() {
        if (this.fechaCarga == null) {
            this.fechaCarga = LocalDateTime.now();
        }
        // Si no se especifica orden, se asigna uno automático
        if (this.ordenVisualizacion == null) {
            this.ordenVisualizacion = 0;
        }
    }
}
