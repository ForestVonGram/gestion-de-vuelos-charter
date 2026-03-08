import { Component, OnInit, OnDestroy, HostListener, Renderer2, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './landing.component.html',
  styleUrls: ['./landing.component.css']
})
export class LandingComponent implements OnInit, OnDestroy {
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
    this.setupReducedMotionPreference();
    this.setupAriaAnnouncements();

    // Check for saved theme preference
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
    if (this.mediaQueryListener) {
      this.mediaQueryListener();
    }
    // Ensure body scroll is restored on destroy
    this.renderer.removeClass(document.body, 'menu-open');
  }

  @HostListener('window:scroll')
  onWindowScroll(): void {
    this.checkScroll();
    this.updateParallax();
  }

  @HostListener('document:keydown.escape')
  onEscapePress(): void {
    if (this.isMobileMenuOpen) {
      this.closeMobileMenu();
    }
  }

  @HostListener('window:resize')
  onWindowResize(): void {
    // Close mobile menu if viewport grows past mobile breakpoint
    if (window.innerWidth > 768 && this.isMobileMenuOpen) {
      this.closeMobileMenu();
    }
  }

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

  private checkSystemTheme(): void {
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)');
    this.isDarkMode = prefersDark.matches;
    if (this.isDarkMode) {
      this.enableDarkMode();
    } else {
      this.disableDarkMode();
    }
  }

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
      if (el) {
        el.style.transform = 'none';
      }
    });
  }

  private enableParallax(): void {
    this.updateParallax();
  }

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

  private checkScroll(): void {
    const heroElement = document.querySelector('.hero');
    if (heroElement) {
      const heroHeight = heroElement.clientHeight;
      this.isNavbarScrolled = window.scrollY > heroHeight - 100;
    } else {
      this.isNavbarScrolled = window.scrollY > 100;
    }
  }

  private updateParallax(): void {
    const mediaQuery = window.matchMedia('(prefers-reduced-motion: reduce)');
    if (mediaQuery.matches) return;

    const statementElement = document.querySelector('.statement');
    if (statementElement) {
      const rect = statementElement.getBoundingClientRect();
      const windowHeight = window.innerHeight;

      if (rect.top < windowHeight && rect.bottom > 0) {
        const scrollProgress = Math.max(0, Math.min(1,
          (windowHeight - rect.top) / (windowHeight + rect.height)
        ));

        this.parallaxOffset = scrollProgress * 350;
        this.applyParallaxTransform();
      }
    }
  }

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

