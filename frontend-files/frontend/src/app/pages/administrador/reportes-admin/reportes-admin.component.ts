import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService, User } from '../../../services/auth/auth.service';
import { AdminSidebarComponent } from '../../../shared/admin-sidebar/admin-sidebar.component';

// --- ENUMS Y DTOs (Reflejo de tu backend Java) ---
export enum TipoReporte {
  OPERATIVO = 'OPERATIVO',
  FLOTA = 'FLOTA',
  HORAS = 'HORAS'
}

export interface ReporteDTO {
  id: number;
  tipo: TipoReporte;
  descripcion: string;
  fechaGeneracion: string | Date;
  fechaInicioRango: string | Date;
  fechaFinRango: string | Date;
  generadoPorNombre: string;
  rutaArchivo?: string;
  datosAgregados?: string;
  numeroRegistros: number;
  observaciones?: string;
}

@Component({
  selector: 'app-reportes-admin',
  standalone: true,
  imports: [CommonModule, RouterModule, AdminSidebarComponent],
  templateUrl: './reportes-admin.component.html',
  styleUrls: ['./reportes-admin.component.css']
})
export class ReportesAdminComponent implements OnInit {

  currentUser: User | null = null;
  reportes: ReporteDTO[] = [];

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    this.currentUser = this.authService.currentUserValue;
    this.cargarDatosSimulados();
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }

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

  // Asignamos una clase de color según el tipo de reporte
  getTipoClase(tipo: TipoReporte): string {
    switch (tipo) {
      case TipoReporte.OPERATIVO: return 'type-operativo'; // Azul
      case TipoReporte.FLOTA: return 'type-flota';         // Morado/Lavanda
      case TipoReporte.HORAS: return 'type-horas';         // Verde
      default: return '';
    }
  }
}
