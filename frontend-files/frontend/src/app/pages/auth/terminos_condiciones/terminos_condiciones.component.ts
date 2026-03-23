import { Component, OnInit, OnDestroy, Renderer2 } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import {AccesibilidadComponent} from '../../../shared/accesibilidad/accesibilidad.component';

@Component({
  selector: 'app-terminos-condiciones',
  templateUrl: './terminos_condiciones.component.html',
  styleUrls: ['./terminos_condiciones.component.css'],
  standalone: true,
  imports: [CommonModule, AccesibilidadComponent]
})
export class TerminosCondicionesComponent implements OnInit, OnDestroy {
  // Estado para controlar si el modo oscuro está activo
  isDarkMode = false;

  constructor(
    private router: Router,
    private renderer: Renderer2 // Renderer2 para manipular el DOM de forma segura
  ) {}

  ngOnInit(): void {
    // Al iniciar, verifica si existe una preferencia de tema guardada en el navegador
    const savedTheme = localStorage.getItem('theme');
    if (savedTheme === 'dark') {
      this.isDarkMode = true;
      this.enableDarkMode();
    }
  }

  ngOnDestroy(): void {
    // Espacio para limpieza de procesos si fuera necesario
  }

  // Alterna entre modo claro y oscuro y guarda la elección
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

  // Agrega las clases CSS necesarias al body y al html para el modo oscuro
  private enableDarkMode(): void {
    this.renderer.addClass(document.body, 'dark-theme');
    this.renderer.addClass(document.documentElement, 'dark-theme-active');
  }

  // Elimina las clases CSS del modo oscuro para volver al tema claro
  private disableDarkMode(): void {
    this.renderer.removeClass(document.body, 'dark-theme');
    this.renderer.removeClass(document.documentElement, 'dark-theme-active');
  }

  // Método para manejar la navegación hacia atrás (pendiente de implementar ruta)
  regresar(): void {
    // Por ejemplo: this.router.navigate(['/auth/register']);
  }
}
