package com.paeldav.backend.infraestructure.repository;

import com.paeldav.backend.domain.entity.Personal;
import com.paeldav.backend.domain.enums.CargoPersonal;
import com.paeldav.backend.domain.enums.EstadoPersonal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad Personal.
 * Proporciona métodos para acceder y manipular datos del personal en la base de datos.
 */
@Repository
public interface PersonalRepository extends JpaRepository<Personal, Long> {

    /**
     * Busca un empleado por su número de empleado.
     * @param numeroEmpleado número único de empleado
     * @return Optional con el personal encontrado o vacío si no existe
     */
    Optional<Personal> findByNumeroEmpleado(String numeroEmpleado);

    /**
     * Verifica si existe un empleado con el número de empleado especificado.
     * @param numeroEmpleado número de empleado a verificar
     * @return true si ya existe un empleado con ese número
     */
    boolean existsByNumeroEmpleado(String numeroEmpleado);

    /**
     * Obtiene todo el personal según su estado.
     * @param estado estado del personal (ACTIVO, INACTIVO, etc.)
     * @return lista de personal con ese estado
     */
    List<Personal> findByEstado(EstadoPersonal estado);

    /**
     * Obtiene todo el personal con un cargo específico.
     * @param cargo cargo del personal (PILOTO, MECANICO, etc.)
     * @return lista de personal con ese cargo
     */
    List<Personal> findByCargo(CargoPersonal cargo);

    /**
     * Obtiene todo el personal con un cargo y estado específicos.
     * @param cargo cargo del personal
     * @param estado estado del personal
     * @return lista de personal que cumple ambos criterios
     */
    List<Personal> findByCargoAndEstado(CargoPersonal cargo, EstadoPersonal estado);

    /**
     * Obtiene todo el personal que se especializa en un área determinada.
     * @param areaEspecializacion área de especialización (ej: "Motores", "Aviónica")
     * @return lista de personal con esa especialización
     */
    List<Personal> findByAreaEspecializacion(String areaEspecializacion);

    /**
     * Busca un empleado por el ID del usuario asociado.
     * @param usuarioId ID del usuario
     * @return Optional con el personal encontrado o vacío si no existe
     */
    Optional<Personal> findByUsuarioId(Long usuarioId);

    /**
     * Busca personal aplicando filtros opcionales.
     * @param nombre nombre del empleado (opcional)
     * @param cargo cargo del empleado (opcional)
     * @param estado estado del empleado (opcional)
     * @return lista de personal que cumple con los filtros aplicados
     */
    @Query("SELECT p FROM Personal p JOIN p.usuario u WHERE " +
            "(:nombre IS NULL OR u.nombre = :nombre) AND " +
            "(:cargo IS NULL OR p.cargo = :cargo) AND " +
            "(:estado IS NULL OR p.estado = :estado)")
    List<Personal> findByFilter(String nombre, CargoPersonal cargo, EstadoPersonal estado);
}