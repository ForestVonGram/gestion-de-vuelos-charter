import { Component } from '@angular/core';
import { HeaderTripulante } from "../../../shared/header-tripulante/header-tripulante.component";
import {AccesibilidadComponent} from '../../../shared/accesibilidad/accesibilidad.component';
import {ChatbotWidgetComponent} from '../../../shared/chatbot-widget/chatbot-widget.component';
import {WhatsAppButtonComponent} from '../../../shared/whatsapp-button/whatsapp-button.component';

@Component({
  selector: 'app-vuelos',
  imports: [HeaderTripulante, AccesibilidadComponent, ChatbotWidgetComponent, WhatsAppButtonComponent],
  templateUrl: './vuelos.component.html',
  styleUrl: './vuelos.component.css',
})
export class VuelosComponent {

  vuelos!: any;

}
