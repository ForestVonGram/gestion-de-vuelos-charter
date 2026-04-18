import { Component, OnInit } from '@angular/core';
import { HeaderTripulante } from "../../../shared/header-tripulante/header-tripulante.component";
import {AccesibilidadComponent} from '../../../shared/accesibilidad/accesibilidad.component';
import {ChatbotWidgetComponent} from '../../../shared/chatbot-widget/chatbot-widget.component';
import {WhatsAppButtonComponent} from '../../../shared/whatsapp-button/whatsapp-button.component';

@Component({
  selector: 'app-tripulacion',
  imports: [HeaderTripulante, AccesibilidadComponent, ChatbotWidgetComponent, WhatsAppButtonComponent],
  templateUrl: './tripulacion.component.html',
  styleUrl: './tripulacion.component.css',
})
export class TripulacionComponent implements OnInit {
  personal!: any;

  ngOnInit(): void {}

}
