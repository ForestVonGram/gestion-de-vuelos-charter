import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService, User } from '../../../services/auth/auth.service';
import { AdminSidebarComponent } from '../../../shared/admin-sidebar/admin-sidebar.component';
import {AccesibilidadComponent} from '../../../shared/accesibilidad/accesibilidad.component';

// --- ENUMS Y DTOs (Basados en tu backend Java) ---

/**
 * Enumeración de estados posibles para un vuelo.
 * Coincide con el enum EstadoVuelo del backend.
 */
export enum EstadoVuelo {
  PENDIENTE = 'PENDIENTE', // Pendiente de aprobación
  APROBADO = 'APROBADO', // Aprobado pero no programado
  PROGRAMADO = 'PROGRAMADO', // Programado con fecha
  EN_VUELO = 'EN_VUELO', // Actualmente en curso
  COMPLETADO = 'COMPLETADO', // Finalizado exitosamente
  CANCELADO = 'CANCELADO' // Cancelado
}

/**
 * DTO que representa un vuelo en el sistema.
 * Coincide con el VueloDTO del backend.
 */
export interface VueloDTO {
  id: number; // Identificador único
  usuarioId: number; // ID del usuario solicitante
  usuarioNombre: string; // Nombre del usuario solicitante
  aeronaveId?: number; // ID de la aeronave asignada (opcional)
  aeronaveMatricula?: string; // Matrícula de la aeronave asignada (opcional)
  tripulacionIds?: number[]; // IDs de la tripulación asignada (opcional)
  origen: string; // Ciudad de origen
  destino: string; // Ciudad de destino
  fechaSalidaProgramada: string | Date; // Fecha y hora programada de salida
  fechaLlegadaProgramada: string | Date; // Fecha y hora programada de llegada
  fechaSalidaReal?: string | Date; // Fecha y hora real de salida (opcional)
  fechaLlegadaReal?: string | Date; // Fecha y hora real de llegada (opcional)
  numeroPasajeros: number; // Número de pasajeros
  estado: EstadoVuelo; // Estado actual del vuelo
  proposito?: string; // Propósito del vuelo (opcional)
  observaciones?: string; // Observaciones adicionales (opcional)
  fechaSolicitud?: string | Date; // Fecha de solicitud (opcional)
  costoEstimado?: number; // Costo estimado del vuelo (opcional)
}

/**
 * Componente que muestra y gestiona los vuelos para administradores.
 * Presenta una tabla con todos los vuelos y su información relevante.
 */
@Component({
  selector: 'app-vuelos-admin',
  standalone: true,
  imports: [CommonModule, RouterModule, AdminSidebarComponent, AccesibilidadComponent],
  templateUrl: './vuelos-admin.component.html',
  styleUrls: ['./vuelos-admin.component.css']
})
export class VuelosAdminComponent implements OnInit {

  // Usuario actualmente autenticado
  currentUser: User | null = null;

  // Lista de vuelos a mostrar en la tabla
  vuelos: VueloDTO[] = [];

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
   * Simula la carga de datos de vuelos desde el backend.
   * TODO: Reemplazar con llamada real al servicio de vuelos.
   */
  cargarDatosSimulados(): void {
    this.vuelos = [
      {
        id: 1045, usuarioId: 1, usuarioNombre: 'Carlos Ruiz', aeronaveMatricula: 'HK-4523',
        origen: 'BOG (Bogotá)', destino: 'MDE (Medellín)',
        fechaSalidaProgramada: new Date(new Date().getTime() + 86400000), // Mañana
        fechaLlegadaProgramada: new Date(new Date().getTime() + 90000000),
        numeroPasajeros: 4, estado: EstadoVuelo.PROGRAMADO, costoEstimado: 2500.00
      },
      {
        id: 1046, usuarioId: 2, usuarioNombre: 'Ana Gómez', aeronaveMatricula: 'HK-8910',
        origen: 'CTG (Cartagena)', destino: 'BOG (Bogotá)',
        fechaSalidaProgramada: new Date(),
        fechaLlegadaProgramada: new Date(new Date().getTime() + 3600000),
        numeroPasajeros: 8, estado: EstadoVuelo.EN_VUELO, costoEstimado: 4200.00
      },
      {
        id: 1047, usuarioId: 3, usuarioNombre: 'Luis Mendoza',
        origen: 'CLO (Cali)', destino: 'BAQ (Barranquilla)',
        fechaSalidaProgramada: new Date(new Date().getTime() + 172800000), // Pasado mañana
        fechaLlegadaProgramada: new Date(new Date().getTime() + 180000000),
        numeroPasajeros: 2, estado: EstadoVuelo.PENDIENTE
      },
      {
        id: 1048, usuarioId: 1, usuarioNombre: 'Carlos Ruiz', aeronaveMatricula: 'HK-3321',
        origen: 'MDE (Medellín)', destino: 'PEI (Pereira)',
        fechaSalidaProgramada: new Date(new Date().getTime() - 86400000), // Ayer
        fechaLlegadaProgramada: new Date(new Date().getTime() - 82800000),
        numeroPasajeros: 5, estado: EstadoVuelo.COMPLETADO, costoEstimado: 1200.00
      },
      {
        id: 1049, usuarioId: 4, usuarioNombre: 'Sofía Castro',
        origen: 'BOG (Bogotá)', destino: 'MIA (Miami)',
        fechaSalidaProgramada: new Date(new Date().getTime() + 432000000),
        fechaLlegadaProgramada: new Date(new Date().getTime() + 446400000),
        numeroPasajeros: 10, estado: EstadoVuelo.CANCELADO
      }
    ];
  }

  /**
   * Obtiene la clase CSS correspondiente al estado del vuelo.
   * @param estado estado del vuelo
   * @returns clase CSS para aplicar estilos
   */
  getEstadoClase(estado: EstadoVuelo): string {
    switch (estado) {
      case EstadoVuelo.EN_VUELO: return 'status-active';
      case EstadoVuelo.COMPLETADO: return 'status-success';
      case EstadoVuelo.PENDIENTE: return 'status-pending';
      case EstadoVuelo.APROBADO: return 'status-approved';
      case EstadoVuelo.PROGRAMADO: return 'status-scheduled';
      case EstadoVuelo.CANCELADO: return 'status-error';
      default: return '';
    }
  }
}
