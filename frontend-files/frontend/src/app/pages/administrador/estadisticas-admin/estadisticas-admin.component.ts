import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService, User } from '../../../services/auth/auth.service';
import { AdminSidebarComponent } from '../../../shared/admin-sidebar/admin-sidebar.component';

// --- DTOs (Espejo de tu backend Java) ---

/**
 * DTO con métricas relacionadas a vuelos.
 */
export interface MetricasVuelosDTO {
  vuelosTotales: number; // Total de vuelos registrados
  vuelosCompletados: number; // Vuelos completados exitosamente
  vuelosEnProceso: number; // Vuelos actualmente en curso
  vuelosCancelados: number; // Vuelos cancelados
  vuelosProgramados: number; // Vuelos programados a futuro
  porcentajeComplecion: number; // Porcentaje de vuelos completados vs total
  ingresoTotalVuelos: number; // Ingresos generados por vuelos
}

/**
 * DTO con métricas relacionadas al personal y tripulación.
 */
export interface MetricasPersonalDTO {
  personalTotal: number; // Total de empleados
  personalActivo: number; // Empleados activos actualmente
  personalEnLicencia: number; // Empleados de licencia
  tripulantesTotal: number; // Total de tripulantes
  tripulantesDisponibles: number; // Tripulantes disponibles para asignar
  tripulantesEnVuelo: number; // Tripulantes actualmente en vuelo
  horasTotalPersonal: number; // Total de horas trabajadas por todo el personal
  horasPromedioPersonal: number; // Promedio de horas por empleado
}

/**
 * DTO con métricas relacionadas a la flota de aeronaves.
 */
export interface MetricasFlotaDTO {
  aeronavesTotales: number; // Total de aeronaves en la flota
  aeronavesActivas: number; // Aeronaves en estado activo
  aeronavesEnMantenimiento: number; // Aeronaves en mantenimiento
  aeronavesDisponibles: number; // Aeronaves disponibles para volar
  porcentajeDisponibilidad: number; // Porcentaje de disponibilidad de la flota
  horasTotalVuelo: number; // Total de horas de vuelo acumuladas
  horasPromedioPorAeronave: number; // Promedio de horas por aeronave
}

/**
 * DTO principal que agrupa todas las métricas del sistema.
 */
export interface MetricasDTO {
  fechaActualizacion: string | Date; // Fecha de la última actualización de métricas
  metricasVuelos: MetricasVuelosDTO; // Métricas de vuelos
  metricasFlota: MetricasFlotaDTO; // Métricas de flota
  metricasPersonal: MetricasPersonalDTO; // Métricas de personal
  rentabilidadPromedio: number; // Porcentaje de rentabilidad promedio
  ocupacionPromedio: number; // Porcentaje de ocupación promedio
}

/**
 * Componente que muestra estadísticas y métricas del sistema para administradores.
 * Proporciona una vista general del rendimiento de vuelos, flota y personal.
 */
@Component({
  selector: 'app-estadisticas-admin',
  standalone: true,
  imports: [CommonModule, RouterModule, AdminSidebarComponent],
  templateUrl: './estadisticas-admin.component.html',
  styleUrls: ['./estadisticas-admin.component.css']
})
export class EstadisticasAdminComponent implements OnInit {

  // Usuario actualmente autenticado
  currentUser: User | null = null;

  // Objeto con todas las métricas del sistema
  metricas!: MetricasDTO;

  /**
   * Constructor del componente
   * @param authService servicio de autenticación
   * @param router servicio de navegación
   */
  constructor(private authService: AuthService, private router: Router) {}

  /**
   * Inicialización del componente.
   * Obtiene el usuario actual y carga los datos simulados.
   */
  ngOnInit(): void {
    this.currentUser = this.authService.currentUserValue;
    this.cargarDatosSimulados();
  }

  /**
   * Cierra la sesión del usuario actual y redirige al login.
   */
  logout(): void {
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }

  /**
   * Simula la respuesta del backend cargando datos de ejemplo.
   * TODO: Reemplazar con llamada real al servicio de estadísticas.
   */
  cargarDatosSimulados(): void {
    this.metricas = {
      fechaActualizacion: new Date(),
      rentabilidadPromedio: 24.5,
      ocupacionPromedio: 85.2,
      metricasVuelos: {
        vuelosTotales: 1250,
        vuelosCompletados: 1100,
        vuelosEnProceso: 12,
        vuelosProgramados: 108,
        vuelosCancelados: 30,
        porcentajeComplecion: 88.0,
        ingresoTotalVuelos: 4500000.00
      },
      metricasFlota: {
        aeronavesTotales: 15,
        aeronavesActivas: 12,
        aeronavesEnMantenimiento: 2,
        aeronavesDisponibles: 10,
        porcentajeDisponibilidad: 80.0,
        horasTotalVuelo: 15420.5,
        horasPromedioPorAeronave: 1028.0
      },
      metricasPersonal: {
        personalTotal: 45,
        personalActivo: 42,
        personalEnLicencia: 3,
        tripulantesTotal: 30,
        tripulantesDisponibles: 12,
        tripulantesEnVuelo: 18,
        horasTotalPersonal: 8500.0,
        horasPromedioPersonal: 188.8
      }
    };
  }
}
