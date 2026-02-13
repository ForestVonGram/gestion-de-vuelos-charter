package com.paeldav.backend.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Clase base abstracta para entidades que requieren control de concurrencia optimista.
 * Proporciona versionado automático para detectar conflictos de actualización.
 */
@MappedSuperclass
@Getter
@Setter
public abstract class VersionedEntity {

    /**
     * Campo de versión para control de concurrencia optimista.
     * Se incrementa automáticamente con cada actualización.
     */
    @Version
    @Column(name = "version")
    private Long version = 0L;

    /**
     * Obtiene la versión actual de la entidad.
     * @return versión actual
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Establece la versión de la entidad.
     * @param version nueva versión
     */
    public void setVersion(Long version) {
        this.version = version;
    }
}
