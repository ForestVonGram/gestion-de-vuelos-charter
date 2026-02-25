import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService, User } from '../../../services/auth/auth.service';

// --- ENUMS Y DTOs (Reflejo de tu backend Java) ---
export enum EstadoNomina {
  BORRADOR = 'BORRADOR',
  PENDIENTE = 'PENDIENTE',
  PAGADA = 'PAGADA',
  RECHAZADA = 'RECHAZADA',
  ANULADA = 'ANULADA'
}

export interface NominaDTO {
  id: number;
  personalId: number;
  personalNombre: string;
  personalApellido: string;
  mes: number;
  ano: number;
  salarioBase: number;
  deducciones: number;
  bonificaciones: number;
  descuentoImpuesto: number;
  descuentoAfiliacion: number;
  totalNeto: number;
  estado: EstadoNomina;
  fechaPago?: string | Date;
  fechaGeneracion: string | Date;
  observaciones?: string;
}

@Component({
  selector: 'app-nomina-admin',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './nomina-admin.component.html',
  styleUrls: ['./nomina-admin.component.css']
})
export class NominaAdminComponent implements OnInit {

  currentUser: User | null = null;
  nominas: NominaDTO[] = [];

  // Nombres de los meses para mostrar en la tabla en lugar del número
  meses = ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'];

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    this.currentUser = this.authService.currentUserValue;
    this.cargarDatosSimulados();
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }

  getNombreMes(mesNumero: number): string {
    return this.meses[mesNumero - 1] || 'N/A';
  }

  cargarDatosSimulados(): void {
    this.nominas = [
      {
        id: 501, personalId: 10, personalNombre: 'Carlos', personalApellido: 'Ruiz',
        mes: 10, ano: 2025, salarioBase: 4500.0, bonificaciones: 300.0, deducciones: 50.0,
        descuentoImpuesto: 450.0, descuentoAfiliacion: 180.0, totalNeto: 4120.0,
        estado: EstadoNomina.PAGADA, fechaGeneracion: new Date('2025-10-25'), fechaPago: new Date('2025-10-30')
      },
      {
        id: 502, personalId: 12, personalNombre: 'Ana', personalApellido: 'Gómez',
        mes: 10, ano: 2025, salarioBase: 3200.0, bonificaciones: 0.0, deducciones: 0.0,
        descuentoImpuesto: 320.0, descuentoAfiliacion: 128.0, totalNeto: 2752.0,
        estado: EstadoNomina.PAGADA, fechaGeneracion: new Date('2025-10-25'), fechaPago: new Date('2025-10-30')
      },
      {
        id: 503, personalId: 15, personalNombre: 'Luis', personalApellido: 'Mendoza',
        mes: 11, ano: 2025, salarioBase: 4500.0, bonificaciones: 500.0, deducciones: 100.0,
        descuentoImpuesto: 480.0, descuentoAfiliacion: 180.0, totalNeto: 4240.0,
        estado: EstadoNomina.PENDIENTE, fechaGeneracion: new Date('2025-11-25')
      },
      {
        id: 504, personalId: 20, personalNombre: 'Sofía', personalApellido: 'Castro',
        mes: 11, ano: 2025, salarioBase: 2800.0, bonificaciones: 150.0, deducciones: 0.0,
        descuentoImpuesto: 280.0, descuentoAfiliacion: 112.0, totalNeto: 2558.0,
        estado: EstadoNomina.BORRADOR, fechaGeneracion: new Date('2025-11-26')
      }
    ];
  }

  getEstadoClase(estado: EstadoNomina): string {
    switch (estado) {
      case EstadoNomina.PAGADA: return 'status-success';
      case EstadoNomina.PENDIENTE: return 'status-pending';
      case EstadoNomina.BORRADOR: return 'status-draft';
      case EstadoNomina.RECHAZADA:
      case EstadoNomina.ANULADA: return 'status-error';
      default: return '';
    }
  }
}
