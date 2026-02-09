package com.paeldav.backend.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad que representa un documento técnico asociado a una aeronave.
 * Almacena referencias a documentos (manuales, certificados, etc.) alojados en Cloudinary.
 */
@Entity
@Table(name = "documentos_tecnicos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentoTecnico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La aeronave es obligatoria")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aeronave_id", nullable = false)
    private Aeronave aeronave;

    @NotBlank(message = "El nombre del documento es obligatorio")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "El tipo de documento es obligatorio")
    @Column(nullable = false)
    private String tipo; // MANUAL, CERTIFICADO, INSPECCION, MANTENIMIENTO, LICENCIA, etc.

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @NotBlank(message = "La URL del documento es obligatoria")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String urlDocumento; // URL en Cloudinary

    @Column(name = "id_cloudinary")
    private String idCloudinary; // ID público de Cloudinary para futuras operaciones

    @NotNull(message = "La fecha de carga es obligatoria")
    @Column(nullable = false)
    private LocalDateTime fechaCarga;

    @Column(name = "fecha_vencimiento")
    private LocalDateTime fechaVencimiento; // Opcional: para documentos que vencen

    @Column(name = "numero_documento")
    private String numeroDocumento; // Número de serie, certificado, etc.

    @Column(name = "tamaño_bytes")
    private Long tamañoBytes;

    @Column(name = "tipo_archivo")
    private String tipoArchivo; // PDF, DOC, JPG, etc.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cargado_por_id")
    private Personal cargadoPor; // Usuario que cargó el documento

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "vigente")
    private Boolean vigente = true;

    @PrePersist
    protected void onCreate() {
        if (this.fechaCarga == null) {
            this.fechaCarga = LocalDateTime.now();
        }
        if (this.vigente == null) {
            this.vigente = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        // Actualizar vigencia basado en fecha de vencimiento si existe
        if (this.fechaVencimiento != null) {
            this.vigente = LocalDateTime.now().isBefore(this.fechaVencimiento);
        }
    }
}
