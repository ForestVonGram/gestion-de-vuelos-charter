import { Component, OnInit, Renderer2 } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../../services/auth/auth.service';
import { AccesibilidadComponent } from '../../../shared/accesibilidad/accesibilidad.component';
import {ChatbotWidgetComponent} from '../../../shared/chatbot-widget/chatbot-widget.component';
import {WhatsAppButtonComponent} from '../../../shared/whatsapp-button/whatsapp-button.component';

interface Vuelo {
  origin: string;
  destination: string;
  date: string;
  status: string;
  statusClass: string;
}

interface Noticia {
  category: string;
  categoryClass: string;
  title: string;
  content: string;
  date: string;
}

interface UserStats {
  totalFlights: number;
  memberSince: string;
  tier: string;
  flightHours: number;
  loyaltyPoints: number;
}

@Component({
  selector: 'app-dashboard-cliente',
  standalone: true,
  imports: [CommonModule, RouterModule, AccesibilidadComponent, ChatbotWidgetComponent, WhatsAppButtonComponent],
  templateUrl: './dashboard-cliente.component.html',
  styleUrls: ['./dashboard-cliente.component.css']
})
export class ClientDashboardComponent implements OnInit {
  // --- Datos de usuario y estado de la interfaz ---
  userName: string = '';
  userEmail: string = '';
  isDropdownOpen: boolean = false;
  isDarkMode: boolean = false;
  welcomeMessage: string = '';

  // --- Estadísticas del usuario (se cargarán del servicio) ---
  userStats: UserStats = {
    totalFlights: 0,
    memberSince: '',
    tier: 'Silver',
    flightHours: 0,
    loyaltyPoints: 0
  };

  // --- Vuelos próximos (se cargarán del servicio) ---
  upcomingFlights: Vuelo[] = [];

  // --- Noticias aéreas (se cargarán del servicio) ---
  newsItems: Noticia[] = [];

  constructor(
    private authService: AuthService,
    private router: Router,
    private renderer: Renderer2
  ) {}

  ngOnInit(): void {
    this.loadUserData();
    this.loadUserStats();
    this.loadUpcomingFlights();
    this.loadNewsItems();
    this.setWelcomeMessage();

    // Restaura la preferencia de tema
    const savedTheme = localStorage.getItem('theme');
    if (savedTheme === 'dark') {
      this.isDarkMode = true;
      this.enableDarkMode();
    }
  }

  private loadUserData(): void {
    const currentUser = this.authService.currentUserValue;
    if (currentUser) {
      this.userName = currentUser.nombreCompleto || currentUser.email?.split('@')[0] || 'Usuario';
      this.userEmail = currentUser.email || '';
    }
  }

  private loadUserStats(): void {
    // Simulación de carga de estadísticas desde el servicio
    // En producción, esto vendría de tu API
    this.userStats = {
      totalFlights: 12,
      memberSince: 'Ene 2025',
      tier: 'Platinum',
      flightHours: 48,
      loyaltyPoints: 15420
    };
  }

  private loadUpcomingFlights(): void {
    // Simulación de carga de vuelos desde el servicio
    // En producción, esto vendría de tu API
    this.upcomingFlights = [
      {
        origin: 'BOG',
        destination: 'MDE',
        date: '15 Feb 2026 - 10:30 AM',
        status: 'Confirmado',
        statusClass: 'status-confirmed'
      },
      {
        origin: 'MDE',
        destination: 'CTG',
        date: '22 Feb 2026 - 14:15 PM',
        status: 'Pendiente',
        statusClass: 'status-pending'
      }
    ];
  }

  private loadNewsItems(): void {
    // Simulación de carga de noticias desde el servicio
    // En producción, esto vendría de tu API
    this.newsItems = [
      {
        category: 'Industria',
        categoryClass: 'category-industria',
        title: 'Nuevas rutas internacionales aprobadas',
        content: 'La Aerocivil aprueba nuevas rutas directas hacia destinos exclusivos en el Caribe y Centroamérica para vuelos chárter.',
        date: 'Hace 2 horas'
      },
      {
        category: 'Seguridad',
        categoryClass: 'category-seguridad',
        title: 'AstraNimbus recibe certificación IS-BAO Stage 3',
        content: 'Nos convertimos en una de las pocas aerolíneas privadas en Latinoamérica con esta prestigiosa certificación de seguridad operacional.',
        date: 'Ayer'
      },
      {
        category: 'Tecnología',
        categoryClass: 'category-tecnologia',
        title: 'Nuevo sistema de gestión de vuelos',
        content: 'Nuestra flota ahora cuenta con la última tecnología en navegación y eficiencia de combustible.',
        date: 'Hace 3 días'
      },
      {
        category: 'Sostenibilidad',
        categoryClass: 'category-sostenibilidad',
        title: 'Programa de compensación de carbono 2026',
        content: 'Lanzamos nuestra iniciativa para compensar el 100% de las emisiones de carbono en todos nuestros vuelos.',
        date: 'Hace 5 días'
      }
    ];
  }

  private setWelcomeMessage(): void {
    const hour = new Date().getHours();
    if (hour < 12) {
      this.welcomeMessage = 'Que tengas un excelente día. ¿Listo para planificar tu próximo vuelo?';
    } else if (hour < 18) {
      this.welcomeMessage = 'Esperamos que estés teniendo una tarde productiva. Tu próxima aventura te espera.';
    } else {
      this.welcomeMessage = 'Buenas noches. ¿Soñando con tu próximo destino?';
    }
  }

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

  private enableDarkMode(): void {
    this.renderer.addClass(document.body, 'dark-theme');
    this.renderer.addClass(document.documentElement, 'dark-theme-active');
  }

  private disableDarkMode(): void {
    this.renderer.removeClass(document.body, 'dark-theme');
    this.renderer.removeClass(document.documentElement, 'dark-theme-active');
  }

  toggleDropdown(): void {
    this.isDropdownOpen = !this.isDropdownOpen;
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }
}
