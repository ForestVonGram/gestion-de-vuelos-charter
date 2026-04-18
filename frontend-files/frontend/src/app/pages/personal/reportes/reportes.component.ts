import { Component, input } from '@angular/core';
import { HeaderTripulante} from '../../../shared/header-tripulante/header-tripulante.component';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';
import {AccesibilidadComponent} from '../../../shared/accesibilidad/accesibilidad.component';
import {ChatbotWidgetComponent} from '../../../shared/chatbot-widget/chatbot-widget.component';
import {WhatsAppButtonComponent} from '../../../shared/whatsapp-button/whatsapp-button.component';

@Component({
  selector: 'app-reportes',
  imports: [HeaderTripulante, ReactiveFormsModule, AccesibilidadComponent, ChatbotWidgetComponent, WhatsAppButtonComponent],
  templateUrl: './reportes.component.html',
  styleUrl: './reportes.component.css',
})
export class ReportesComponent {

  reportes = input<any[]>([]);
  createReportForm!: FormGroup;


  constructor(private formBuilder: FormBuilder) {
    this.createForm();
  }

  private createForm() {
    this.createReportForm = this.formBuilder.group({
      nombre: ['',[Validators.required]],
      descripcion: ['',[Validators.required]],
      fechaVuelo: ['',[Validators.required]],
      duracionVuelo: ['',[Validators.required]],
      observaciones: ['',[Validators.required]],
      gravedad: ['',[Validators.required]],
    });
  }

  createReport() {
    console.log("Create reporte")
  }
}
