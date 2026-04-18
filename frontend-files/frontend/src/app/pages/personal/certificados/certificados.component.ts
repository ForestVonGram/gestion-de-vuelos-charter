import { Component, input } from '@angular/core';
import { HeaderTripulante } from "../../../shared/header-tripulante/header-tripulante.component";
import {AccesibilidadComponent} from '../../../shared/accesibilidad/accesibilidad.component';
import {ChatbotWidgetComponent} from '../../../shared/chatbot-widget/chatbot-widget.component';
import {WhatsAppButtonComponent} from '../../../shared/whatsapp-button/whatsapp-button.component';

@Component({
  selector: 'app-certificados',
  imports: [HeaderTripulante, AccesibilidadComponent, ChatbotWidgetComponent, WhatsAppButtonComponent],
  templateUrl: './certificados.component.html',
  styleUrl: './certificados.component.css',
})
export class CertificadosComponent {

  certificados = input<any[]>([]);

  onFileSelected(event: any) {
    const file: File = event.target.files[0];
    if (file) {
      console.log("Selected file:", file.name);
    }
  }
}
