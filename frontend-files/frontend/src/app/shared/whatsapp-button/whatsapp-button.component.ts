import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-whatsapp-button',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './whatsapp-button.component.html',
  styleUrls: ['./whatsapp-button.component.css']
})
export class WhatsAppButtonComponent implements OnInit, OnDestroy {
  showNotification = true;
  private intervalId: any;
  private timeoutId: any;

  // Número de WhatsApp (cámbialo por el tuyo)
  private readonly phoneNumber = '573016973283';

  // Mensaje predeterminado
  private readonly defaultMessage = 'Hola%2C%20vengo%20de%20AstraNimbus%20Aviation%20y%20necesito%20ayuda';

  constructor() { }

  ngOnInit(): void {
    // Opción 1: La animación es permanente (siempre destella)
    // No hace nada extra, la animación está siempre activa por CSS

    // Opción 2: La animación se detiene después de 30 segundos
    // this.timeoutId = setTimeout(() => {
    //   this.showNotification = false;
    // }, 30000);

    // Opción 3: Animación intermitente (destella cada 10 segundos)
    this.startIntermittentPulse();
  }

  ngOnDestroy(): void {
    if (this.intervalId) {
      clearInterval(this.intervalId);
    }
    if (this.timeoutId) {
      clearTimeout(this.timeoutId);
    }
  }

  // Obtener la URL de WhatsApp
  getWhatsAppUrl(): string {
    return `https://wa.me/${this.phoneNumber}?text=${this.defaultMessage}`;
  }

  // Opcional: Animación intermitente (destella cada cierto tiempo)
  private startIntermittentPulse(): void {
    this.intervalId = setInterval(() => {
      this.showNotification = true;
      setTimeout(() => {
        this.showNotification = false;
      }, 2000);
    }, 10000);
  }
}
