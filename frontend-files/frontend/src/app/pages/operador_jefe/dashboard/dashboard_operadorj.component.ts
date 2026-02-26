import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../services/auth/auth.service';

@Component({
  selector: 'app-dashboard-operadorj',
  templateUrl: './dashboard_operadorj.component.html',
  styleUrls: ['./dashboard_operadorj.component.css'],
  standalone: true,
  imports: [CommonModule, RouterModule]
})
export class DashboardOperadorJComponent implements OnInit {
  userName: string = 'Operador Jefe';
  userEmail: string = '';
  esAdmin: boolean = false;
  isDropdownOpen: boolean = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.cargarDatosUsuario();
  }

  cargarDatosUsuario(): void {
    const currentUser = this.authService.currentUserValue;
    if (currentUser) {
      this.userName = currentUser.nombreCompleto || 'Operador Jefe';
      this.userEmail = currentUser.email || '';
      this.esAdmin = currentUser.rol === 'ADMINISTRADOR';
    }
  }

  toggleDropdown(): void {
    this.isDropdownOpen = !this.isDropdownOpen;
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }

  // Navegación a los diferentes módulos
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

  irAAdmin(): void {
    this.router.navigate(['/admin/dashboard']);
  }
}
