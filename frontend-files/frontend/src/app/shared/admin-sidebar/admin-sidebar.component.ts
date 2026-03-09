import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../services/auth/auth.service';

@Component({
  selector: 'app-admin-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule], // Necesario para [routerLink] y directivas básicas
  templateUrl: './admin-sidebar.component.html',
  styleUrls: ['./admin-sidebar.component.css']
})
export class AdminSidebarComponent {

  constructor(
    private authService: AuthService, // Para gestionar el cierre de sesión
    private router: Router             // Para redirigir al usuario tras el logout
  ) {}

  /**
   * Finaliza la sesión administrativa.
   * Llama al servicio de autenticación para limpiar tokens y
   * redirige automáticamente a la pantalla de acceso.
   */
  logout(): void {
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }
}
