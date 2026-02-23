import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService, User} from '../../../services/auth/auth.service';
import { AdminSidebarComponent } from '../../../shared/admin-sidebar/admin-sidebar.component';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, AdminSidebarComponent],
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit {

  // Usuario tipado correctamente con la interfaz de tu backend
  currentUser: User | null = null;
  fechaActual = new Date();

  // KPIs / Estadísticas
  stats = [
    { title: 'VuelosComponent Activos', value: '12', icon: 'fas fa-plane-departure', color: 'var(--color-info)' },
    { title: 'Ingresos (Mes)', value: '$45,200', icon: 'fas fa-dollar-sign', color: 'var(--color-success)' },
    { title: 'Nuevos Usuarios', value: '18', icon: 'fas fa-users', color: 'var(--color-warning)' },
    { title: 'Incidencias', value: '2', icon: 'fas fa-exclamation-triangle', color: 'var(--color-error)' }
  ];

  // Datos simulados de vuelos
  recentFlights = [
    { id: 'FL-2023', pilot: 'Cap. Morgan', route: 'BOG - MIA', status: 'En Vuelo', time: '2h 15m' },
    { id: 'FL-2024', pilot: 'Cap. Rogers', route: 'MDE - CTG', status: 'Programado', time: '---' },
    { id: 'FL-2025', pilot: 'Cap. Danvers', route: 'CLO - PTY', status: 'Completado', time: '1h 45m' },
    { id: 'FL-2026', pilot: 'Cap. Stark', route: 'BOG - JFK', status: 'Cancelado', time: '---' }
  ];

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    // Obtenemos el usuario que inició sesión
    this.currentUser = this.authService.currentUserValue;
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }

  // Método para asignar clases de colores según el estado del vuelo
  getStatusClass(status: string): string {
    switch(status) {
      case 'En Vuelo': return 'status-active';
      case 'Completado': return 'status-success';
      case 'Cancelado': return 'status-error';
      default: return 'status-pending';
    }
  }
}
