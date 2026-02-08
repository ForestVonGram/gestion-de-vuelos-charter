package com.paeldav.backend.infraestructure.repository;

import com.paeldav.backend.domain.entity.Pago;
import com.paeldav.backend.domain.enums.EstadoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Pago.
 * Proporciona métodos para acceder a los datos de pagos en la base de datos.
 */
@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    /**
     * Obtiene un pago por su referencia de MercadoPago.
     *
     * @param referenciaMercadoPago referencia única de MercadoPago
     * @return Optional con el pago encontrado
     */
    Optional<Pago> findByReferenciaMercadoPago(String referenciaMercadoPago);

    /**
     * Obtiene todos los pagos de un vuelo específico.
     *
     * @param vueloId ID del vuelo
     * @return lista de pagos del vuelo
     */
    List<Pago> findByVueloId(Long vueloId);

    /**
     * Obtiene todos los pagos de un usuario específico.
     *
     * @param usuarioId ID del usuario
     * @return lista de pagos del usuario
     */
    List<Pago> findByUsuarioId(Long usuarioId);

    /**
     * Obtiene todos los pagos en un estado específico.
     *
     * @param estado estado del pago
     * @return lista de pagos con el estado especificado
     */
    List<Pago> findByEstado(EstadoPago estado);

    /**
     * Obtiene todos los pagos confirmados de un vuelo.
     *
     * @param vueloId ID del vuelo
     * @return lista de pagos confirmados del vuelo
     */
    @Query("SELECT p FROM Pago p WHERE p.vuelo.id = :vueloId AND p.estado = 'CONFIRMADO'")
    List<Pago> findConfirmedPaymentsByFlight(@Param("vueloId") Long vueloId);

    /**
     * Obtiene los pagos de un vuelo en un rango de fechas.
     *
     * @param vueloId ID del vuelo
     * @param startDate fecha de inicio
     * @param endDate fecha de fin
     * @return lista de pagos del vuelo en el rango de fechas
     */
    @Query("SELECT p FROM Pago p WHERE p.vuelo.id = :vueloId " +
           "AND p.fechaPago BETWEEN :startDate AND :endDate")
    List<Pago> findPaymentsByFlightAndDateRange(
            @Param("vueloId") Long vueloId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Obtiene el total de pagos confirmados para un vuelo.
     *
     * @param vueloId ID del vuelo
     * @return total de montos pagados
     */
    @Query("SELECT SUM(p.monto) FROM Pago p WHERE p.vuelo.id = :vueloId AND p.estado = 'CONFIRMADO'")
    Double getTotalConfirmedAmountForFlight(@Param("vueloId") Long vueloId);

    /**
     * Obtiene un pago por su número de preferencia de MercadoPago.
     *
     * @param numeroPreferencia número de preferencia
     * @return Optional con el pago encontrado
     */
    Optional<Pago> findByNumeroPreferencia(String numeroPreferencia);
}
