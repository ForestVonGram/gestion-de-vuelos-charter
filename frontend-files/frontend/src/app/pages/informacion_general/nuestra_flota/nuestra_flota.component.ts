import { Component, OnInit, OnDestroy, HostListener, Renderer2 } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AccesibilidadComponent} from '../../../shared/accesibilidad/accesibilidad.component';
import {ChatbotWidgetComponent} from '../../../shared/chatbot-widget/chatbot-widget.component';
import {WhatsAppButtonComponent} from '../../../shared/whatsapp-button/whatsapp-button.component';

@Component({
  selector: 'app-nuestra-flota',
  standalone: true,
  imports: [CommonModule, RouterModule, AccesibilidadComponent, ChatbotWidgetComponent, WhatsAppButtonComponent],
  templateUrl: './nuestra_flota.component.html',
  styleUrls: ['./nuestra_flota.component.css']
})
export class NuestraFlotaComponent implements OnInit, OnDestroy {
  // --- Estados de la interfaz ---
  isNavbarScrolled = false; // Controla si la navegación debe cambiar de estilo al hacer scroll
  isDarkMode = false;       // Estado del tema visual

  constructor(private renderer: Renderer2) {}

  ngOnInit(): void {
    this.checkScroll(); // Verifica la posición inicial del scroll al cargar

    // Recupera la preferencia del tema desde el almacenamiento local
    const savedTheme = localStorage.getItem('theme');
    if (savedTheme === 'dark') {
      this.isDarkMode = true;
      this.enableDarkMode();
    }
  }

  ngOnDestroy(): void {
    // Espacio para limpieza de eventos si fuera necesario
  }

  // Escucha el evento de scroll en la ventana global para actualizar la UI
  @HostListener('window:scroll')
  onWindowScroll(): void {
    this.checkScroll();
  }

  // Alterna el modo oscuro y notifica a otros posibles componentes interesados
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

  // Activa el modo oscuro y dispara un evento personalizado 'darkmode-change'
  private enableDarkMode(): void {
    this.renderer.addClass(document.body, 'dark-theme');
    this.renderer.addClass(document.documentElement, 'dark-theme-active');
    window.dispatchEvent(new Event('darkmode-change'));
  }

  // Desactiva el modo oscuro y dispara un evento personalizado 'darkmode-change'
  private disableDarkMode(): void {
    this.renderer.removeClass(document.body, 'dark-theme');
    this.renderer.removeClass(document.documentElement, 'dark-theme-active');
    window.dispatchEvent(new Event('darkmode-change'));
  }

  // Lógica para determinar cuándo la barra de navegación debe volverse opaca o cambiar de estilo
  private checkScroll(): void {
    const heroElement = document.querySelector('.fleet-hero');
    if (heroElement) {
      // Si existe un elemento 'hero', el cambio ocurre al salir de su área visible
      const heroHeight = heroElement.clientHeight;
      this.isNavbarScrolled = window.scrollY > heroHeight - 100;
    } else {
      // Valor por defecto si no se encuentra el elemento principal
      this.isNavbarScrolled = window.scrollY > 100;
    }
  }
}
