// src/app/components/dashboard-cliente/dashboard-cliente.component.ts
import { Component, OnInit, Renderer2, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../../services/auth/auth.service';
import { VueloService, VueloDTO } from '../../../services/vuelos/vuelo.service';
import { AccesibilidadComponent } from '../../../shared/accesibilidad/accesibilidad.component';
import { ChatbotWidgetComponent } from '../../../shared/chatbot-widget/chatbot-widget.component';
import { WhatsAppButtonComponent } from '../../../shared/whatsapp-button/whatsapp-button.component';
import { finalize } from 'rxjs/operators';

interface Vuelo {
  id: number;               // <- NUEVO
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

  // Estado de carga unificado
  loadingData: boolean = true;
  dataError: boolean = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    private renderer: Renderer2,
    private vueloService: VueloService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    console.log('=== [Dashboard] ngOnInit ===');
    this.loadUserData();
    this.loadAllVuelosData(); // Una sola llamada para todo
    this.loadNewsItems();
    this.setWelcomeMessage();

    const savedTheme = localStorage.getItem('theme');
    if (savedTheme === 'dark') {
      this.isDarkMode = true;
      this.enableDarkMode();
    }
  }

  private loadUserData(): void {
    const currentUser = this.authService.currentUserValue;
    console.log('[Dashboard] Usuario actual:', currentUser);
    if (currentUser) {
      this.userName = currentUser.nombreCompleto || currentUser.email?.split('@')[0] || 'Usuario';
      this.userEmail = currentUser.email || '';
    }
  }

  /**
   * Carga los vuelos del usuario y actualiza tanto estadísticas como próximos vuelos.
   * Esto evita múltiples peticiones y reduce el tiempo de renderizado.
   */
  private loadAllVuelosData(): void {
    const currentUser = this.authService.currentUserValue;
    const userId = currentUser?.userId;
    console.log('[Dashboard] Iniciando carga de vuelos para userId:', userId);

    if (!userId) {
      console.warn('[Dashboard] No hay userId');
      this.loadingData = false;
      this.dataError = true;
      return;
    }

    this.vueloService.getVuelosPorUsuario(userId)
      .pipe(
        finalize(() => {
          this.loadingData = false;
          this.cdr.detectChanges(); // Forzar actualización de la vista
          console.log('[Dashboard] Finalizada la carga de vuelos');
        })
      )
      .subscribe({
        next: (vuelos) => {
          console.log('[Dashboard] ✅ Vuelos recibidos:', vuelos.length);
          this.processVuelosData(vuelos);
          this.dataError = false;
        },
        error: (err) => {
          console.error('[Dashboard] ❌ Error cargando vuelos:', err);
          this.dataError = true;
          this.upcomingFlights = [];
          this.userStats.totalFlights = 0;
        }
      });
  }

  private processVuelosData(vuelos: VueloDTO[]): void {
    // --- Estadísticas del usuario ---
    this.userStats.totalFlights = vuelos.length;

    const horasTotales = vuelos.reduce((acc, v) => {
      if (v.fechaSalidaReal && v.fechaLlegadaReal) {
        const diff = new Date(v.fechaLlegadaReal).getTime() - new Date(v.fechaSalidaReal).getTime();
        return acc + diff / (1000 * 60 * 60);
      }
      return acc;
    }, 0);
    this.userStats.flightHours = Math.round(horasTotales);
    this.userStats.loyaltyPoints = vuelos.length * 100;

    if (vuelos.length >= 20) this.userStats.tier = 'Platinum';
    else if (vuelos.length >= 10) this.userStats.tier = 'Gold';
    else if (vuelos.length >= 5) this.userStats.tier = 'Silver';
    else this.userStats.tier = 'Bronze';

    // --- Últimos vuelos añadidos (los más recientes) ---
    // Filtramos vuelos cancelados para no mostrarlos (opcional)
    const vuelosValidos = vuelos.filter(v => v.estado !== 'CANCELADO');

    // Ordenar por fecha de solicitud descendente (más reciente primero)
    const ultimosVuelos = vuelosValidos
      .sort((a, b) =>
        new Date(b.fechaSolicitud).getTime() - new Date(a.fechaSolicitud).getTime()
      )
      .slice(0, 2);

    console.log('[Dashboard] Últimos 2 vuelos añadidos:', ultimosVuelos);

    this.upcomingFlights = ultimosVuelos.map(v => ({
      id: v.id,                // <- NUEVO
      origin: this.extraerCodigo(v.origen),
      destination: this.extraerCodigo(v.destino),
      date: this.formatearFechaVuelo(v.fechaSalidaProgramada),
      status: this.traducirEstado(v.estado),
      statusClass: this.obtenerClaseEstado(v.estado)
    }));

    // Forzar actualización de la vista
    this.cdr.detectChanges();
  }

  // Métodos auxiliares (sin cambios)
  private extraerCodigo(lugar: string): string {
    if (!lugar) return '???';
    const match = lugar.match(/\(([^)]+)\)/);
    return match ? match[1] : lugar.substring(0, 3).toUpperCase();
  }

  private formatearFechaVuelo(iso: string): string {
    const fecha = new Date(iso);
    return fecha.toLocaleString('es-CO', {
      day: '2-digit',
      month: 'short',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
      hour12: true
    });
  }

  private traducirEstado(estado: string): string {
    const estados: Record<string, string> = {
      'SOLICITADO': 'Pendiente',
      'CONFIRMADO': 'Confirmado',
      'EN_CURSO': 'En curso',
      'COMPLETADO': 'Finalizado',
      'CANCELADO': 'Cancelado',
      'DEMORADO': 'Demorado'
    };
    return estados[estado] || estado;
  }

  private obtenerClaseEstado(estado: string): string {
    switch (estado) {
      case 'CONFIRMADO': return 'status-confirmed';
      case 'SOLICITADO': return 'status-pending';
      case 'DEMORADO': return 'status-delayed';
      default: return 'status-pending';
    }
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

  // ---------- NUEVOS MÉTODOS PARA BOTONES ----------
  verDetalles(vueloId: number): void {
    this.router.navigate(['/vuelo', vueloId]);
  }

  cancelarVuelo(vueloId: number, event: Event): void {
    event.stopPropagation(); // Evitar que se active el clic del contenedor
    if (confirm('¿Estás seguro de que deseas cancelar este vuelo? Esta acción no se puede deshacer.')) {
      this.vueloService.cancelarVuelo(vueloId).subscribe({
        next: () => {
          alert('Vuelo cancelado exitosamente.');
          this.loadAllVuelosData(); // Recargar los datos
        },
        error: (err) => {
          console.error('Error al cancelar vuelo:', err);
          alert('No se pudo cancelar el vuelo. Intenta nuevamente.');
        }
      });
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
