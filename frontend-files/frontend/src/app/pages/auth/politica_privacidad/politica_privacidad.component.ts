import { Component, OnInit, OnDestroy, HostListener, Renderer2 } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-politica-privacidad',
  templateUrl: './politica_privacidad.component.html',
  styleUrls: ['./politica_privacidad.component.css'],
  standalone: true,
  imports: [CommonModule]
})
export class PoliticaPrivacidadComponent implements OnInit, OnDestroy {
  isDarkMode = false;

  constructor(
    private router: Router,
    private renderer: Renderer2
  ) {}

  ngOnInit(): void {
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

  regresar(): void {
    // Navega de vuelta a la página anterior (generalmente registro)
    this.router.navigate(['/auth/register']);
  }
}
