import { Component } from '@angular/core';
import { RouterLink } from "@angular/router";
import { AuthService } from '../../services/auth/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-header-tripulante',
  standalone: true, // Arquitectura modular e independiente
  imports: [RouterLink], // Permite la navegación declarativa en el HTML
  templateUrl: './header-tripulante.component.html',
  styleUrl: './header-tripulante.component.css',
})
export class HeaderTripulante {

  // --- Datos de Identidad ---
  userName!: string;  // Nombre extraído del token/auth para mostrar en el saludo
  userEmail!: string; // Email del tripulante para el menú desplegable
  isDropdownOpen: boolean = false; // Control de visibilidad del menú de perfil

  constructor(
    private authService: AuthService, // Inyección para acceder al estado de sesión
    private router: Router             // Inyección para redirección tras logout
  ) {
    // Al instanciar el componente, recuperamos los datos del usuario actual
    const currentUser = this.authService.currentUserValue;
    if (currentUser) {
      this.userName = currentUser.nombreCompleto;
      this.userEmail = currentUser.email;
    }
  }

  /**
   * Cambia el estado de visibilidad del menú desplegable del usuario (perfil/cerrar sesión).
   */
  toggleDropdown() {
    console.log("Toggle dropdown");
    this.isDropdownOpen = !this.isDropdownOpen;
  }

  /**
   * Ejecuta el proceso de salida segura.
   * Llama al método logout del servicio para borrar tokens y limpia la navegación.
   */
  logOut(): void {
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }
}
