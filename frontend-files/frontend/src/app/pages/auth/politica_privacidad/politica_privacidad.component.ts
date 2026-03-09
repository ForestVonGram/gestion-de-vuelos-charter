import { Component, OnInit, OnDestroy, HostListener, Renderer2 } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  // Selector que permite usar el componente en HTML como <app-politica-privacidad>
  selector: 'app-politica-privacidad',

  // Ruta del archivo HTML asociado al componente
  templateUrl: './politica_privacidad.component.html',

  // Archivo de estilos específico para este componente
  styleUrls: ['./politica_privacidad.component.css'],

  // Indica que es un componente standalone (no requiere módulo Angular)
  standalone: true,

  // Módulos que utiliza el componente
  imports: [CommonModule]
})
export class PoliticaPrivacidadComponent implements OnInit, OnDestroy {

  // Variable que controla si el modo oscuro está activo
  isDarkMode = false;

  constructor(

    // Router para navegar entre rutas del sistema
    private router: Router,

    // Renderer2 permite manipular el DOM de forma segura en Angular
    private renderer: Renderer2

  ) {}

  // =========================
  // MÉTODO DE INICIALIZACIÓN
  // =========================
  ngOnInit(): void {

    // Verifica si existe una preferencia de tema guardada en el navegador
    const savedTheme = localStorage.getItem('theme');

    // Si el usuario tenía activado el modo oscuro previamente
    if (savedTheme === 'dark') {

      // Activa el modo oscuro
      this.isDarkMode = true;

      // Aplica las clases CSS correspondientes
      this.enableDarkMode();
    }

  }

  // =========================
  // MÉTODO DE DESTRUCCIÓN
  // =========================
  ngOnDestroy(): void {

    // Espacio reservado para limpieza de recursos
    // (listeners, timers, suscripciones, etc.)
  }

  // =========================
  // CAMBIAR MODO OSCURO
  // =========================
  toggleDarkMode(): void {

    // Cambia el estado actual del modo oscuro
    this.isDarkMode = !this.isDarkMode;

    // Si se activa el modo oscuro
    if (this.isDarkMode) {

      // Aplica las clases de estilo oscuro
      this.enableDarkMode();

      // Guarda la preferencia en el navegador
      localStorage.setItem('theme', 'dark');

    } else {

      // Si se desactiva el modo oscuro
      this.disableDarkMode();

      // Guarda la preferencia como modo claro
      localStorage.setItem('theme', 'light');
    }

  }

  // =========================
  // ACTIVAR MODO OSCURO
  // =========================
  private enableDarkMode(): void {

    // Agrega clase al body
    this.renderer.addClass(document.body, 'dark-theme');

    // Agrega clase al html raíz
    this.renderer.addClass(document.documentElement, 'dark-theme-active');

  }

  // =========================
  // DESACTIVAR MODO OSCURO
  // =========================
  private disableDarkMode(): void {

    // Elimina clase del body
    this.renderer.removeClass(document.body, 'dark-theme');

    // Elimina clase del html raíz
    this.renderer.removeClass(document.documentElement, 'dark-theme-active');

  }

  // =========================
  // REGRESAR A REGISTRO
  // =========================
  regresar(): void {

    // Navega de vuelta a la página de registro
    this.router.navigate(['/auth/register']);

  }
}
