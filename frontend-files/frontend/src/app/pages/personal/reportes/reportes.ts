import { Component, input } from '@angular/core';
import { HeaderTripulante } from "../../../shared/header-tripulante/header-tripulante";
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-reportes',
  imports: [HeaderTripulante, ReactiveFormsModule],
  templateUrl: './reportes.html',
  styleUrl: './reportes.css',
})
export class Reportes {

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
