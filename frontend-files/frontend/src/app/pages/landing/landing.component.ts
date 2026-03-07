import { Component, OnInit, OnDestroy, HostListener, Renderer2 } from '@angular/core';
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

  constructor(private renderer: Renderer2) {}

  ngOnInit(): void {
    this.checkScroll();

    // Check for saved theme preference
    const savedTheme = localStorage.getItem('theme');
    if (savedTheme === 'dark') {
      this.isDarkMode = true;
      this.enableDarkMode();
    }
  }

  ngOnDestroy(): void {
    // Cleanup
  }

  @HostListener('window:scroll')
  onWindowScroll(): void {
    this.checkScroll();
    this.updateParallax();
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

    // También agregar clase al html para mayor especificidad (opcional, pero buena práctica)
    this.renderer.addClass(document.documentElement, 'dark-theme-active');

    // Disparar evento para que otros componentes puedan reaccionar
    window.dispatchEvent(new Event('darkmode-change'));
  }

  private disableDarkMode(): void {
    // Remover clases actualizadas
    this.renderer.removeClass(document.body, 'dark-theme');
    this.renderer.removeClass(document.documentElement, 'dark-theme-active');

    // Disparar evento
    window.dispatchEvent(new Event('darkmode-change'));
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
    const statementElement = document.querySelector('.statement');
    if (statementElement) {
      const rect = statementElement.getBoundingClientRect();
      const elementTop = rect.top;
      const windowHeight = window.innerHeight;

      if (elementTop < windowHeight && elementTop > -rect.height) {
        const scrollProgress = (windowHeight - elementTop) / (windowHeight + rect.height);
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
