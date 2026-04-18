import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService, User } from '../../../services/auth/auth.service';
import { AdminSidebarComponent } from '../../../shared/admin-sidebar/admin-sidebar.component';
import {AccesibilidadComponent} from '../../../shared/accesibilidad/accesibilidad.component';
import {ChatbotWidgetComponent} from '../../../shared/chatbot-widget/chatbot-widget.component';
import {WhatsAppButtonComponent} from '../../../shared/whatsapp-button/whatsapp-button.component';

// --- ENUMS Y DTOs (Reflejo de tu backend Java) ---

/**
 * Enumeración de tipos de reporte disponibles.
 * Coincide con el enum TipoReporte del backend.
 */
export enum TipoReporte {
  OPERATIVO = 'OPERATIVO', // Reporte operativo de vuelos
  FLOTA = 'FLOTA', // Reporte de flota de aeronaves
  HORAS = 'HORAS' // Reporte de horas trabajadas por tripulación
}

/**
 * DTO que representa un reporte generado en el sistema.
 * Coincide con el ReporteDTO del backend.
 */
export interface ReporteDTO {
  id: number; // Identificador único del reporte
  tipo: TipoReporte; // Tipo de reporte
  descripcion: string; // Descripción del reporte
  fechaGeneracion: string | Date; // Fecha de generación
  fechaInicioRango: string | Date; // Fecha inicio del rango consultado
  fechaFinRango: string | Date; // Fecha fin del rango consultado
  generadoPorNombre: string; // Nombre de quien generó el reporte
  rutaArchivo?: string; // Ruta del archivo generado (opcional)
  datosAgregados?: string; // Datos agregados del reporte (opcional)
  numeroRegistros: number; // Número de registros incluidos
  observaciones?: string; // Observaciones adicionales (opcional)
}

/**
 * Componente que muestra y gestiona los reportes generados en el sistema.
 * Presenta una tabla con todos los reportes y su información relevante.
 */
@Component({
  selector: 'app-reportes-admin',
  standalone: true,
  imports: [CommonModule, RouterModule, AdminSidebarComponent, AccesibilidadComponent, ChatbotWidgetComponent, WhatsAppButtonComponent],
  templateUrl: './reportes-admin.component.html',
  styleUrls: ['./reportes-admin.component.css']
})
export class ReportesAdminComponent implements OnInit {

  // Usuario actualmente autenticado
  currentUser: User | null = null;

  // Lista de reportes a mostrar en la tabla
  reportes: ReporteDTO[] = [];

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
   * Simula la carga de datos de reportes desde el backend.
   * TODO: Reemplazar con llamada real al servicio de reportes.
   */
  cargarDatosSimulados(): void {
    this.reportes = [
      {
        id: 3001, tipo: TipoReporte.OPERATIVO,
        descripcion: 'Resumen de vuelos e ingresos mensuales',
        fechaGeneracion: new Date('2026-02-01T08:30:00'),
        fechaInicioRango: new Date('2026-01-01T00:00:00'),
        fechaFinRango: new Date('2026-01-31T23:59:59'),
        generadoPorNombre: 'Admin Principal', numeroRegistros: 145
      },
      {
        id: 3002, tipo: TipoReporte.FLOTA,
        descripcion: 'Estado de mantenimiento y horas de vuelo',
        fechaGeneracion: new Date('2026-02-15T10:15:00'),
        fechaInicioRango: new Date('2025-08-01T00:00:00'),
        fechaFinRango: new Date('2026-02-15T23:59:59'),
        generadoPorNombre: 'Carlos Ruiz', numeroRegistros: 15
      },
      {
        id: 3003, tipo: TipoReporte.HORAS,
        descripcion: 'Horas de tripulación para cálculo de nómina',
        fechaGeneracion: new Date('2026-02-20T16:45:00'),
        fechaInicioRango: new Date('2026-01-15T00:00:00'),
        fechaFinRango: new Date('2026-02-15T23:59:59'),
        generadoPorNombre: 'Ana Gómez', numeroRegistros: 42
      }
    ];
  }

  /**
   * Obtiene la clase CSS correspondiente al tipo de reporte.
   * @param tipo tipo de reporte
   * @returns clase CSS para aplicar estilos
   */
  getTipoClase(tipo: TipoReporte): string {
    switch (tipo) {
      case TipoReporte.OPERATIVO: return 'type-operativo'; // Azul
      case TipoReporte.FLOTA: return 'type-flota';         // Morado/Lavanda
      case TipoReporte.HORAS: return 'type-horas';         // Verde
      default: return '';
    }
  }
}
