import { Component, OnInit, OnDestroy, HostListener, Renderer2 } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AccesibilidadComponent } from '../../../shared/accesibilidad/accesibilidad.component';
import {ChatbotWidgetComponent} from '../../../shared/chatbot-widget/chatbot-widget.component';
import {WhatsAppButtonComponent} from '../../../shared/whatsapp-button/whatsapp-button.component';

@Component({
  selector: 'app-sobre-nosotros',
  standalone: true,
  imports: [CommonModule, RouterModule, AccesibilidadComponent, ChatbotWidgetComponent, WhatsAppButtonComponent],
  templateUrl: './sobre_nosotros.component.html',
  styleUrls: ['./sobre_nosotros.component.css']
})
export class SobreNosotrosComponent implements OnInit, OnDestroy {
  // --- Estados de UI ---
  isNavbarScrolled = false; // Controla el cambio de estilo de la barra de navegación
  isDarkMode = false;       // Estado actual del tema (claro/oscuro)

  constructor(private renderer: Renderer2) {}

  ngOnInit(): void {
    this.checkScroll(); // Verifica la posición del scroll al cargar la página

    // Recupera la preferencia de tema guardada en el navegador
    const savedTheme = localStorage.getItem('theme');
    if (savedTheme === 'dark') {
      this.isDarkMode = true;
      this.enableDarkMode();
    }
  }

  ngOnDestroy(): void {
    // Limpieza de procesos si fuera necesario al destruir el componente
  }

  // Escucha el evento de scroll global para actualizar la apariencia de la Navbar
  @HostListener('window:scroll')
  onWindowScroll(): void {
    this.checkScroll();
  }

  // Alterna entre los temas y guarda la preferencia del usuario
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

  // Activa el modo oscuro aplicando clases al DOM y notificando mediante un evento global
  private enableDarkMode(): void {
    this.renderer.addClass(document.body, 'dark-theme');
    this.renderer.addClass(document.documentElement, 'dark-theme-active');
    window.dispatchEvent(new Event('darkmode-change'));
  }

  // Desactiva el modo oscuro y notifica el cambio de tema
  private disableDarkMode(): void {
    this.renderer.removeClass(document.body, 'dark-theme');
    this.renderer.removeClass(document.documentElement, 'dark-theme-active');
    window.dispatchEvent(new Event('darkmode-change'));
  }

  // Calcula si el usuario ha hecho scroll más allá de la sección inicial (Hero)
  private checkScroll(): void {
    const heroElement = document.querySelector('.about-hero');
    if (heroElement) {
      // Ajusta el estado de la Navbar basado en la altura del elemento Hero
      const heroHeight = heroElement.clientHeight;
      this.isNavbarScrolled = window.scrollY > heroHeight - 100;
    } else {
      // Valor de respaldo por si no se encuentra el elemento .about-hero
      this.isNavbarScrolled = window.scrollY > 100;
    }
  }
}
