import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common'; // Importante para *ngFor
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../../services/auth/auth.service'; // 2. Importar AuthService (ajusta la ruta si es necesario)

@Component({
  selector: 'app-client-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './dashboard-cliente.component.html',
  styleUrls: ['./dashboard-cliente.component.css']
})
export class ClientDashboardComponent implements OnInit {

  // Datos simulados del usuario (esto vendría de tu AuthService)
  userName: string = 'Juanito Pérez';
  userEmail: string = '';
  isDropdownOpen: boolean = false;

  // Datos simulados para la flota (imágenes circulares)
  // Asegúrate de tener imágenes en tu carpeta assets o usar URLs externas
  fleetImages: string[] = [
    'assets/images/plane1.jpg', // Reemplaza con tus rutas reales
    'assets/images/plane2.jpg',
    'assets/images/plane3.jpg',
    'assets/images/plane4.jpg'
  ];

  constructor(
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit(): void {

    const currentUser = this.authService.currentUserValue;
    if (currentUser) {
      this.userName = currentUser.nombreCompleto || currentUser.email;
      this.userEmail = currentUser.email || '';
    }
  }

  toggleDropdown(): void {
    this.isDropdownOpen = !this.isDropdownOpen;
  }

  onSearchFlight(): void {
    console.log('Buscando vuelo...');
    // Aquí iría la lógica de redirección o búsqueda
  }

  // 4. Crear el método de cerrar sesión
  logout(): void {
    this.authService.logout(); // Limpia token y localstorage
    this.router.navigate(['/auth/login']); // Redirige al login
  }
}
