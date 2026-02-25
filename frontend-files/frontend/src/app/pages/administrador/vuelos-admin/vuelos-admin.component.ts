import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService, User } from '../../../services/auth/auth.service';

// --- ENUMS Y DTOs (Basados en tu backend Java) ---
export enum EstadoVuelo {
  PENDIENTE = 'PENDIENTE',
  APROBADO = 'APROBADO',
  PROGRAMADO = 'PROGRAMADO',
  EN_VUELO = 'EN_VUELO',
  COMPLETADO = 'COMPLETADO',
  CANCELADO = 'CANCELADO'
}

export interface VueloDTO {
  id: number;
  usuarioId: number;
  usuarioNombre: string;
  aeronaveId?: number;
  aeronaveMatricula?: string;
  tripulacionIds?: number[];
  origen: string;
  destino: string;
  fechaSalidaProgramada: string | Date;
  fechaLlegadaProgramada: string | Date;
  fechaSalidaReal?: string | Date;
  fechaLlegadaReal?: string | Date;
  numeroPasajeros: number;
  estado: EstadoVuelo;
  proposito?: string;
  observaciones?: string;
  fechaSolicitud?: string | Date;
  costoEstimado?: number;
}

@Component({
  selector: 'app-vuelos-admin',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './vuelos-admin.component.html',
  styleUrls: ['./vuelos-admin.component.css']
})
export class VuelosAdminComponent implements OnInit {

  currentUser: User | null = null;
  vuelos: VueloDTO[] = [];

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

  // Asigna un color al badge dependiendo del estado del vuelo
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
