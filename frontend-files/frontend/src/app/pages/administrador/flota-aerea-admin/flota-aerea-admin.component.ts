import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService, User } from '../../../services/auth/auth.service'; // Ajusta la ruta si es necesario
import { AdminSidebarComponent } from '../../../shared/admin-sidebar/admin-sidebar.component';
import {AccesibilidadComponent} from '../../../shared/accesibilidad/accesibilidad.component';

// --- ENUMS Y DTOs ---

/**
 * Enumeración de estados posibles para una aeronave.
 * Coincide con el enum EstadoAeronave del backend.
 */
export enum EstadoAeronave {
  OPERATIVA = 'OPERATIVA',
  EN_MANTENIMIENTO = 'EN_MANTENIMIENTO',
  FUERA_DE_SERVICIO = 'FUERA_DE_SERVICIO'
}

/**
 * DTO que representa una aeronave en el sistema.
 * Coincide con el AeronaveDTO del backend.
 */
export interface AeronaveDTO {
  id: number; // Identificador único
  matricula: string; // Matrícula de la aeronave
  modelo: string; // Modelo
  fabricante: string; // Fabricante
  capacidadPasajeros: number; // Capacidad de pasajeros
  capacidadTripulacion: number; // Capacidad de tripulación
  autonomiaKm: number; // Autonomía en kilómetros
  velocidadCruceroKmh: number; // Velocidad de crucero
  fechaFabricacion: string; // Fecha de fabricación
  fechaUltimaRevision: string; // Fecha última revisión
  horasVueloTotales: number; // Total de horas de vuelo acumuladas
  estado: EstadoAeronave; // Estado actual
  especificacionesTecnicas?: string; // Especificaciones técnicas (opcional)
  imagenes: any[]; // Lista de imágenes de la aeronave
}

/**
 * Componente que muestra y gestiona la flota aérea para administradores.
 * Presenta una tabla con todas las aeronaves y su información relevante.
 */
@Component({
  selector: 'app-flota-aerea-admin',
  standalone: true,
  imports: [CommonModule, RouterModule, AdminSidebarComponent, AccesibilidadComponent],
  templateUrl: './flota-aerea-admin.component.html',
  styleUrls: ['./flota-aerea-admin.component.css']
})
export class FlotaAereaAdminComponent implements OnInit {

  // Usuario actualmente autenticado
  currentUser: User | null = null;

  // Lista de aeronaves a mostrar en la tabla
  aeronaves: AeronaveDTO[] = [];

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
   * Simula la carga de datos de aeronaves desde el backend.
   * TODO: Reemplazar con llamada real al servicio de aeronaves.
   */
  cargarDatosSimulados(): void {
    this.aeronaves = [
      {
        id: 1, matricula: 'HK-4523', modelo: 'Citation CJ4', fabricante: 'Cessna',
        capacidadPasajeros: 10, capacidadTripulacion: 2, autonomiaKm: 4010, velocidadCruceroKmh: 835,
        fechaFabricacion: '2019-05-12', fechaUltimaRevision: '2025-11-20', horasVueloTotales: 1250.5,
        estado: EstadoAeronave.OPERATIVA, imagenes: []
      },
      {
        id: 2, matricula: 'HK-8910', modelo: 'Phenom 300E', fabricante: 'Embraer',
        capacidadPasajeros: 9, capacidadTripulacion: 2, autonomiaKm: 3723, velocidadCruceroKmh: 859,
        fechaFabricacion: '2021-02-18', fechaUltimaRevision: '2026-01-10', horasVueloTotales: 840.0,
        estado: EstadoAeronave.OPERATIVA, imagenes: []
      },
      {
        id: 3, matricula: 'HK-3321', modelo: 'Seneca V', fabricante: 'Piper',
        capacidadPasajeros: 5, capacidadTripulacion: 1, autonomiaKm: 1533, velocidadCruceroKmh: 348,
        fechaFabricacion: '2015-08-30', fechaUltimaRevision: '2026-02-01', horasVueloTotales: 3450.2,
        estado: EstadoAeronave.EN_MANTENIMIENTO, imagenes: []
      },
      {
        id: 4, matricula: 'HK-7742', modelo: 'Gulfstream G280', fabricante: 'Gulfstream',
        capacidadPasajeros: 10, capacidadTripulacion: 2, autonomiaKm: 6667, velocidadCruceroKmh: 850,
        fechaFabricacion: '2022-11-05', fechaUltimaRevision: '2025-12-15', horasVueloTotales: 520.8,
        estado: EstadoAeronave.OPERATIVA, imagenes: []
      },
      {
        id: 5, matricula: 'HK-1105', modelo: 'Baron G58', fabricante: 'Beechcraft',
        capacidadPasajeros: 5, capacidadTripulacion: 1, autonomiaKm: 2741, velocidadCruceroKmh: 374,
        fechaFabricacion: '2018-04-22', fechaUltimaRevision: '2025-09-10', horasVueloTotales: 2100.0,
        estado: EstadoAeronave.OPERATIVA, imagenes: []
      },
      {
        id: 6, matricula: 'HK-9988', modelo: 'Challenger 350', fabricante: 'Bombardier',
        capacidadPasajeros: 9, capacidadTripulacion: 2, autonomiaKm: 5926, velocidadCruceroKmh: 870,
        fechaFabricacion: '2016-10-14', fechaUltimaRevision: '2025-05-20', horasVueloTotales: 4120.5,
        estado: EstadoAeronave.FUERA_DE_SERVICIO, imagenes: []
      }
    ];
  }

  /**
   * Obtiene la clase CSS correspondiente al estado de la aeronave.
   * @param estado estado de la aeronave
   * @returns clase CSS para aplicar estilos
   */
  getEstadoClase(estado: EstadoAeronave): string {
    switch (estado) {
      case EstadoAeronave.OPERATIVA: return 'status-success';
      case EstadoAeronave.EN_MANTENIMIENTO: return 'status-pending';
      case EstadoAeronave.FUERA_DE_SERVICIO: return 'status-error';
      default: return '';
    }
  }
}
