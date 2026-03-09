import { Component, OnInit, Renderer2 } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../../services/auth/auth.service';

@Component({
  selector: 'app-client-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './dashboard-cliente.component.html',
  styleUrls: ['./dashboard-cliente.component.css']
})
export class ClientDashboardComponent implements OnInit {
  // --- Datos de usuario y estado de la interfaz ---
  userName: string = 'Juanito Pérez';
  userEmail: string = '';
  isDropdownOpen: boolean = false;
  isDarkMode: boolean = false;

  // Galería de imágenes para la sección de flota del cliente
  fleetImages: string[] = [
    'assets/images/plane1.jpg',
    'assets/images/plane2.jpg',
    'assets/images/plane3.jpg',
    'assets/images/plane4.jpg'
  ];

  constructor(
    private authService: AuthService,
    private router: Router,
    private renderer: Renderer2 // Renderer2 para manipulación segura de clases globales
  ) { }

  ngOnInit(): void {
    // Recupera la información del usuario autenticado desde el servicio
    const currentUser = this.authService.currentUserValue;
    if (currentUser) {
      this.userName = currentUser.nombreCompleto || currentUser.email;
      this.userEmail = currentUser.email || '';
    }

    // Al cargar, restaura la preferencia de tema (oscuro/claro) del almacenamiento local
    const savedTheme = localStorage.getItem('theme');
    if (savedTheme === 'dark') {
      this.isDarkMode = true;
      this.enableDarkMode();
    }
  }

  // Alterna el estado del modo oscuro y persiste la elección en el navegador
  toggleDarkMode(): void {
    this.isDarkMode = !this.isDarkMode;
    if (this.isDarkMode) {
      this.enableDarkMode();
      localStorage.setItem('theme', 'dark');
    } else {
      this.disableDarkMode();
      localStorage.setItem('theme', 'light');
    }
  }

  // Aplica clases de tema oscuro a la raíz del documento (HTML y Body)
  private enableDarkMode(): void {
    this.renderer.addClass(document.body, 'dark-theme');
    this.renderer.addClass(document.documentElement, 'dark-theme-active');
  }

  // Remueve las clases de tema oscuro para volver al tema base (claro)
  private disableDarkMode(): void {
    this.renderer.removeClass(document.body, 'dark-theme');
    this.renderer.removeClass(document.documentElement, 'dark-theme-active');
  }

  // Controla la visibilidad del menú desplegable del perfil de usuario
  toggleDropdown(): void {
    this.isDropdownOpen = !this.isDropdownOpen;
  }

  // Acción para el buscador de vuelos (lógica pendiente de implementar)
  onSearchFlight(): void {
    console.log('Buscando vuelo...');
  }

  // Finaliza la sesión del usuario y lo redirige a la pantalla de login
  logout(): void {
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }
}
