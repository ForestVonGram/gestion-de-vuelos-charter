import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService, User } from '../../../services/auth/auth.service';
import { AdminSidebarComponent } from '../../../shared/admin-sidebar/admin-sidebar.component';
import {AccesibilidadComponent} from '../../../shared/accesibilidad/accesibilidad.component';

// --- ENUMS Y DTOs (Reflejo de tu backend Java) ---

/**
 * Enumeración de estados posibles para una nómina.
 * Coincide con el enum EstadoNomina del backend.
 */
export enum EstadoNomina {
  BORRADOR = 'BORRADOR', // Nómina en borrador
  PENDIENTE = 'PENDIENTE', // Nómina pendiente de pago
  PAGADA = 'PAGADA', // Nómina pagada
  RECHAZADA = 'RECHAZADA', // Nómina rechazada
  ANULADA = 'ANULADA' // Nómina anulada
}

/**
 * DTO que representa una nómina en el sistema.
 * Coincide con el NominaDTO del backend.
 */
export interface NominaDTO {
  id: number; // Identificador único
  personalId: number; // ID del empleado
  personalNombre: string; // Nombre del empleado
  personalApellido: string; // Apellido del empleado
  mes: number; // Mes de la nómina (1-12)
  ano: number; // Año de la nómina
  salarioBase: number; // Salario base
  deducciones: number; // Deducciones aplicadas
  bonificaciones: number; // Bonificaciones adicionales
  descuentoImpuesto: number; // Descuento por impuestos
  descuentoAfiliacion: number; // Descuento por afiliación
  totalNeto: number; // Total neto a pagar
  estado: EstadoNomina; // Estado actual
  fechaPago?: string | Date; // Fecha en que se pagó (opcional)
  fechaGeneracion: string | Date; // Fecha de generación
  observaciones?: string; // Observaciones adicionales (opcional)
}

/**
 * Componente que muestra y gestiona las nóminas del personal para administradores.
 * Presenta una tabla con todas las nóminas generadas y su información relevante.
 */
@Component({
  selector: 'app-nomina-admin',
  standalone: true,
  imports: [CommonModule, RouterModule, AdminSidebarComponent, AccesibilidadComponent],
  templateUrl: './nomina-admin.component.html',
  styleUrls: ['./nomina-admin.component.css']
})
export class NominaAdminComponent implements OnInit {

  // Usuario actualmente autenticado
  currentUser: User | null = null;

  // Lista de nóminas a mostrar en la tabla
  nominas: NominaDTO[] = [];

  // Nombres de los meses para mostrar en la tabla en lugar del número
  meses = ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'];

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
   * Convierte el número de mes a su nombre abreviado.
   * @param mesNumero número del mes (1-12)
   * @returns nombre abreviado del mes o 'N/A' si es inválido
   */
  getNombreMes(mesNumero: number): string {
    return this.meses[mesNumero - 1] || 'N/A';
  }

  /**
   * Simula la carga de datos de nóminas desde el backend.
   * TODO: Reemplazar con llamada real al servicio de nóminas.
   */
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

  /**
   * Obtiene la clase CSS correspondiente al estado de la nómina.
   * @param estado estado de la nómina
   * @returns clase CSS para aplicar estilos
   */
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
