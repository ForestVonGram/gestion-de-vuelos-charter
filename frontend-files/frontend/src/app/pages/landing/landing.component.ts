import { Component, OnInit, OnDestroy, HostListener, Renderer2, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AccesibilidadComponent } from '../../shared/accesibilidad/accesibilidad.component';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [CommonModule, RouterModule, AccesibilidadComponent],
  templateUrl: './landing.component.html',
  styleUrls: ['./landing.component.css']
})
export class LandingComponent implements OnInit, OnDestroy {
  // --- Estados de UI y Animación ---
  isNavbarScrolled = false;
  parallaxOffset = 0;
  isDarkMode = false;
  isMobileMenuOpen = false;
  private mediaQueryListener: (() => void) | null = null;

  constructor(
    private renderer: Renderer2,
    private elementRef: ElementRef
  ) {}

  ngOnInit(): void {
    this.checkScroll();
    // Configura accesibilidad y preferencias de movimiento al iniciar
    this.setupReducedMotionPreference();
    this.setupAriaAnnouncements();

    // Lógica de jerarquía para el tema: 1. LocalStorage, 2. Preferencia del Sistema
    const savedTheme = localStorage.getItem('theme');
    if (savedTheme === 'dark') {
      this.isDarkMode = true;
      this.enableDarkMode();
    } else if (savedTheme === 'light') {
      this.isDarkMode = false;
      this.disableDarkMode();
    } else {
      this.checkSystemTheme();
    }
  }

  ngOnDestroy(): void {
    // Limpieza de listeners de medios y restauración del scroll del body
    if (this.mediaQueryListener) {
      this.mediaQueryListener();
    }
    this.renderer.removeClass(document.body, 'menu-open');
  }

  // Escucha el scroll para actualizar la Navbar y el efecto Parallax
  @HostListener('window:scroll')
  onWindowScroll(): void {
    this.checkScroll();
    this.updateParallax();
  }

  // Cierra el menú móvil al presionar la tecla Escape (Accesibilidad)
  @HostListener('document:keydown.escape')
  onEscapePress(): void {
    if (this.isMobileMenuOpen) {
      this.closeMobileMenu();
    }
  }

  // Cierra el menú móvil si se cambia el tamaño de la ventana a escritorio
  @HostListener('window:resize')
  onWindowResize(): void {
    if (window.innerWidth > 768 && this.isMobileMenuOpen) {
      this.closeMobileMenu();
    }
  }

  // Gestiona la apertura/cierre del menú móvil y bloquea el scroll del body
  toggleMobileMenu(): void {
    this.isMobileMenuOpen = !this.isMobileMenuOpen;
    if (this.isMobileMenuOpen) {
      this.renderer.addClass(document.body, 'menu-open');
      this.announceToScreenReader('Menú abierto');
    } else {
      this.renderer.removeClass(document.body, 'menu-open');
      this.announceToScreenReader('Menú cerrado');
    }
  }

  closeMobileMenu(): void {
    this.isMobileMenuOpen = false;
    this.renderer.removeClass(document.body, 'menu-open');
  }

  // Alterna modo oscuro y notifica auditivamente a usuarios con lectores de pantalla
  toggleDarkMode(): void {
    this.isDarkMode = !this.isDarkMode;
    if (this.isDarkMode) {
      this.enableDarkMode();
      localStorage.setItem('theme', 'dark');
      this.announceToScreenReader('Modo oscuro activado');
    } else {
      this.disableDarkMode();
      localStorage.setItem('theme', 'light');
      this.announceToScreenReader('Modo claro activado');
    }
  }

  private enableDarkMode(): void {
    this.renderer.addClass(document.body, 'dark-theme');
    this.renderer.addClass(document.documentElement, 'dark-theme-active');
    window.dispatchEvent(new Event('darkmode-change'));
  }

  private disableDarkMode(): void {
    this.renderer.removeClass(document.body, 'dark-theme');
    this.renderer.removeClass(document.documentElement, 'dark-theme-active');
    window.dispatchEvent(new Event('darkmode-change'));
  }

  // Detecta si el sistema operativo del usuario prefiere modo oscuro
  private checkSystemTheme(): void {
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)');
    this.isDarkMode = prefersDark.matches;
    if (this.isDarkMode) {
      this.enableDarkMode();
    } else {
      this.disableDarkMode();
    }
  }

  // Deshabilita animaciones si el usuario tiene activada la opción "Reducir movimiento" en su OS
  private setupReducedMotionPreference(): void {
    const mediaQuery = window.matchMedia('(prefers-reduced-motion: reduce)');
    const handleReducedMotion = (e: MediaQueryListEvent | MediaQueryList) => {
      if (e.matches) {
        this.disableParallax();
      } else {
        this.enableParallax();
      }
    };

    handleReducedMotion(mediaQuery);
    mediaQuery.addEventListener('change', handleReducedMotion);
    this.mediaQueryListener = () => mediaQuery.removeEventListener('change', handleReducedMotion);
  }

  private disableParallax(): void {
    const elements = ['.cloud-large', '.cloud-small', '.aircraft'];
    elements.forEach(selector => {
      const el = this.elementRef.nativeElement.querySelector(selector);
      if (el) { el.style.transform = 'none'; }
    });
  }

  private enableParallax(): void {
    this.updateParallax();
  }

  // Crea un elemento invisible para enviar notificaciones de voz a lectores de pantalla (ARIA)
  private setupAriaAnnouncements(): void {
    let announcer = document.getElementById('aria-announcer');
    if (!announcer) {
      announcer = this.renderer.createElement('div');
      this.renderer.setAttribute(announcer, 'id', 'aria-announcer');
      this.renderer.setAttribute(announcer, 'aria-live', 'polite');
      this.renderer.setAttribute(announcer, 'aria-atomic', 'true');
      this.renderer.addClass(announcer, 'visually-hidden');
      this.renderer.appendChild(document.body, announcer);
    }
  }

  private announceToScreenReader(message: string): void {
    const announcer = document.getElementById('aria-announcer');
    if (announcer) {
      announcer.textContent = message;
      setTimeout(() => {
        if (announcer) announcer.textContent = '';
      }, 3000);
    }
  }

  // Controla el estado visual de la Navbar según el scroll relativo al Hero
  private checkScroll(): void {
    const heroElement = document.querySelector('.hero');
    if (heroElement) {
      const heroHeight = heroElement.clientHeight;
      this.isNavbarScrolled = window.scrollY > heroHeight - 100;
    } else {
      this.isNavbarScrolled = window.scrollY > 100;
    }
  }

  // Calcula el progreso del scroll para mover elementos a diferentes velocidades (Parallax)
  private updateParallax(): void {
    const mediaQuery = window.matchMedia('(prefers-reduced-motion: reduce)');
    if (mediaQuery.matches) return;

    const statementElement = document.querySelector('.statement');
    if (statementElement) {
      const rect = statementElement.getBoundingClientRect();
      const windowHeight = window.innerHeight;

      // Solo calcula si el elemento es visible en pantalla
      if (rect.top < windowHeight && rect.bottom > 0) {
        const scrollProgress = Math.max(0, Math.min(1,
          (windowHeight - rect.top) / (windowHeight + rect.height)
        ));

        this.parallaxOffset = scrollProgress * 350;
        this.applyParallaxTransform();
      }
    }
  }

  // Aplica las transformaciones CSS para mover nubes y aviones de forma asíncrona
  private applyParallaxTransform(): void {
    const cloudLarge = document.querySelector('.cloud-large') as HTMLElement;
    const cloudSmall = document.querySelector('.cloud-small') as HTMLElement;
    const aircraft = document.querySelector('.aircraft') as HTMLElement;

    if (cloudLarge) {
      cloudLarge.style.transform = `translateY(${this.parallaxOffset * 0.2}px) scale(1.05)`;
    }
    if (cloudSmall) {
      cloudSmall.style.transform = `translateY(${this.parallaxOffset * 0.4}px) scale(1.03)`;
    }
    if (aircraft) {
      aircraft.style.transform = `translateY(${this.parallaxOffset * 0.1}px) scale(1.02)`;
    }
  }
}

