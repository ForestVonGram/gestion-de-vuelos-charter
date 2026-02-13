package com.paeldav.backend.domain.exception;

/**
 * Excepción lanzada cuando se detecta un conflicto de concurrencia en la modificación de datos.
 * Indica que otra transacción ha modificado la misma entidad desde que fue cargada.
 */
public class ConcurrentDataModificationException extends RuntimeException {

    private Long entityId;
    private String entityType;
    private Long currentVersion;
    private Long expectedVersion;

    public ConcurrentDataModificationException(String message) {
        super(message);
    }

    public ConcurrentDataModificationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConcurrentDataModificationException(String message, Long entityId, String entityType,
                                               Long currentVersion, Long expectedVersion) {
        super(message);
        this.entityId = entityId;
        this.entityType = entityType;
        this.currentVersion = currentVersion;
        this.expectedVersion = expectedVersion;
    }

    public Long getEntityId() {
        return entityId;
    }

    public String getEntityType() {
        return entityType;
    }

    public Long getCurrentVersion() {
        return currentVersion;
    }

    public Long getExpectedVersion() {
        return expectedVersion;
    }

    @Override
    public String toString() {
        return "ConcurrentDataModificationException{" +
                "entityId=" + entityId +
                ", entityType='" + entityType + '\'' +
                ", currentVersion=" + currentVersion +
                ", expectedVersion=" + expectedVersion +
                ", message=" + getMessage() +
                '}';
    }
}
