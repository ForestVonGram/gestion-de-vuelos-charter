import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService, User } from '../../../services/auth/auth.service'; // Ajusta la ruta si es necesario
import { AdminSidebarComponent } from '../../../shared/admin-sidebar/admin-sidebar.component';
import {AccesibilidadComponent} from '../../../shared/accesibilidad/accesibilidad.component';
import { EstadoAeronave } from '../../../models/avion/estado-avion';
import { AeronaveDTO } from '../../../models/avion/avion';
import { Aeronave } from '../../../services/vuelos/aeronave_service';
import { ChangeDetectorRef } from '@angular/core';
import Swal from 'sweetalert2';
import {ChatbotWidgetComponent} from '../../../shared/chatbot-widget/chatbot-widget.component';
import {WhatsAppButtonComponent} from '../../../shared/whatsapp-button/whatsapp-button.component';
// --- ENUMS Y DTOs ---

/**
 * Enumeración de estados posibles para una aeronave.
 * Coincide con el enum EstadoAeronave del backend.
 */

/**


/**
 * Componente que muestra y gestiona la flota aérea para administradores.
 * Presenta una tabla con todas las aeronaves y su información relevante.
 */
@Component({
  selector: 'app-flota-aerea-admin',
  standalone: true,
  imports: [CommonModule, RouterModule, AdminSidebarComponent, AccesibilidadComponent, ChatbotWidgetComponent, WhatsAppButtonComponent],
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
   * @param avionService servicio para operaciones con aeronaves
   */
  constructor(private authService: AuthService, private router: Router, private avionService: Aeronave,
    private cdr: ChangeDetectorRef
  ) {}

  /**
   * Inicialización del componente.
   * Obtiene el usuario actual y carga los datos simulados.
   */
  ngOnInit(): void {
    this.currentUser = this.authService.currentUserValue;
    this.obtenerAeronaves();
  }

  /**
   * Cierra la sesión del usuario actual y redirige al login.
   */
  logout(): void {
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }

  obtenerAeronaves(): void {
    this.avionService.getAeronaves().subscribe({
      next: (response) => {
        this.aeronaves = response;
        this.cdr.detectChanges(); // Actualizar la vista con los datos obtenidos
      },
      error: (error) => {
        console.error('Error al obtener aeronaves:', error);
        Swal.fire('Error', 'No se pudieron cargar las aeronaves. Intente nuevamente más tarde.', 'error');
      }
    });

  }

  /**
   * Obtiene la clase CSS correspondiente al estado de la aeronave.
   * @param estado estado de la aeronave
   * @returns clase CSS para aplicar estilos
   */
  getEstadoClase(estado: EstadoAeronave): string {
    switch (estado) {
      case EstadoAeronave.DISPONIBLE: return 'status-success';
      case EstadoAeronave.EN_MANTENIMIENTO: return 'status-pending';
      case EstadoAeronave.FUERA_DE_SERVICIO: return 'status-error';
      case EstadoAeronave.EN_VUELO: return 'status-in-flight';
      default: return '';
    }
  }
}
