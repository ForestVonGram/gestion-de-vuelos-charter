import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../services/auth/auth.service';
import {AccesibilidadComponent} from '../../../shared/accesibilidad/accesibilidad.component';

@Component({
  selector: 'app-dashboard-operadorj',
  templateUrl: './dashboard_operadorj.component.html',
  styleUrls: ['./dashboard_operadorj.component.css'],
  standalone: true,
  imports: [CommonModule, RouterModule, AccesibilidadComponent]
})
export class DashboardOperadorJComponent implements OnInit {
  // --- Propiedades de Perfil y Estado ---
  userName: string = 'Operador Jefe';
  userEmail: string = '';
  esAdmin: boolean = false; // Define si el usuario tiene privilegios administrativos
  isDropdownOpen: boolean = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.cargarDatosUsuario(); // Al iniciar, recupera la identidad del usuario
  }

  // Extrae los datos del usuario logueado para personalizar la vista y permisos
  cargarDatosUsuario(): void {
    const currentUser = this.authService.currentUserValue;
    if (currentUser) {
      this.userName = currentUser.nombreCompleto || 'Operador Jefe';
      this.userEmail = currentUser.email || '';
      // Verificación de rol para mostrar opciones administrativas si aplica
      this.esAdmin = currentUser.rol === 'ADMINISTRADOR';
    }
  }

  // Gestiona la apertura y cierre del menú de perfil
  toggleDropdown(): void {
    this.isDropdownOpen = !this.isDropdownOpen;
  }

  // Finaliza la sesión y redirige al usuario a la pantalla de acceso
  logout(): void {
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }

  // --- Módulo de Navegación ---
  // Estos métodos centralizan el acceso a las diferentes secciones de la app

  irAMantenimiento(): void {
    this.router.navigate(['/operador/mantenimiento']);
  }

  irAMantenimientosOfrecidos(): void {
    this.router.navigate(['/operador/mantenimientos/ofrecidos']);
  }

  irATripulacion(): void {
    this.router.navigate(['/tripulacion']);
  }

  irATripulante(): void {
    this.router.navigate(['/tripulante']);
  }

  irAVuelos(): void {
    this.router.navigate(['/vuelos']);
  }

  irACertificados(): void {
    this.router.navigate(['/certificados']);
  }

  irAReportes(): void {
    this.router.navigate(['/reportes']);
  }

  // Acceso especial al panel administrativo si el rol lo permite
  irAAdmin(): void {
    this.router.navigate(['/admin/dashboard']);
  }
}
