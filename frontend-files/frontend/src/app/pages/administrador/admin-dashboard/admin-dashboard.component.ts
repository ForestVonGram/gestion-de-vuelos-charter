import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService, User} from '../../../services/auth/auth.service';
import { AdminSidebarComponent } from '../../../shared/admin-sidebar/admin-sidebar.component';
import {AccesibilidadComponent} from '../../../shared/accesibilidad/accesibilidad.component';
import {ChatbotWidgetComponent} from '../../../shared/chatbot-widget/chatbot-widget.component';
import {WhatsAppButtonComponent} from '../../../shared/whatsapp-button/whatsapp-button.component';

/**
 * Componente del dashboard principal para administradores.
 * Muestra estadísticas, vuelos recientes y opciones de gestión del sistema.
 */
@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, AdminSidebarComponent, AccesibilidadComponent, ChatbotWidgetComponent, WhatsAppButtonComponent],
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit {

  // Usuario actualmente autenticado (del backend)
  currentUser: User | null = null;

  // Fecha actual para mostrar en el dashboard
  fechaActual = new Date();

  // KPIs / Estadísticas para mostrar en tarjetas
  stats = [
    { title: 'Vuelos Activos', value: '12', icon: 'fas fa-plane-departure', color: 'var(--color-info)' },
    { title: 'Ingresos (Mes)', value: '$45,200', icon: 'fas fa-dollar-sign', color: 'var(--color-success)' },
    { title: 'Nuevos Usuarios', value: '18', icon: 'fas fa-users', color: 'var(--color-warning)' },
    { title: 'Incidencias', value: '2', icon: 'fas fa-exclamation-triangle', color: 'var(--color-error)' }
  ];

  // Datos simulados de vuelos recientes para mostrar en tabla
  recentFlights = [
    { id: 'FL-2023', pilot: 'Cap. Morgan', route: 'BOG - MIA', status: 'En Vuelo', time: '2h 15m' },
    { id: 'FL-2024', pilot: 'Cap. Rogers', route: 'MDE - CTG', status: 'Programado', time: '---' },
    { id: 'FL-2025', pilot: 'Cap. Danvers', route: 'CLO - PTY', status: 'Completado', time: '1h 45m' },
    { id: 'FL-2026', pilot: 'Cap. Stark', route: 'BOG - JFK', status: 'Cancelado', time: '---' }
  ];

  /**
   * Constructor del componente
   * @param authService servicio de autenticación
   * @param router servicio de navegación
   */
  constructor(private authService: AuthService, private router: Router) {}

  /**
   * Inicialización del componente.
   * Obtiene el usuario actualmente autenticado.
   */
  ngOnInit(): void {
    // Obtenemos el usuario que inició sesión
    this.currentUser = this.authService.currentUserValue;
  }

  /**
   * Cierra la sesión del usuario actual y redirige al login.
   */
  logout(): void {
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }

  /**
   * Método para asignar clases de colores según el estado del vuelo.
   * @param status estado del vuelo
   * @returns clase CSS correspondiente al estado
   */
  getStatusClass(status: string): string {
    switch(status) {
      case 'En Vuelo': return 'status-active';
      case 'Completado': return 'status-success';
      case 'Cancelado': return 'status-error';
      default: return 'status-pending';
    }
  }
}
