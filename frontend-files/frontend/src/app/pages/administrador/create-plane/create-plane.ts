import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule} from '@angular/forms';
import { EstadoAeronave } from '../flota-aerea-admin/flota-aerea-admin.component';

@Component({
  selector: 'app-create-plane',
  imports: [ReactiveFormsModule],
  templateUrl: './create-plane.html',
  styleUrl: './create-plane.css',
})
export class CreatePlane {

  avionForm!: FormGroup;
  submitted = false;
  estados = Object.values(EstadoAeronave);

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {

    this.avionForm = this.fb.group({

      matricula: ['', Validators.required],
      modelo: ['', Validators.required],

      fabricante: [''],
      capacidadPasajeros: [null, Validators.required],
      capacidadTripulacion: [null, Validators.required],
      autonomiaKm: [null, Validators.required],
      velocidadCruceroKmh: [null, Validators.required],
      fechaFabricacion: ['', Validators.required],
      fechaUltimaRevision: ['', Validators.required],
      estado: ['ACTIVO'],
      especificacionesTecnicas: ['',Validators.required]
    });

  }

  crearAvion(){

    this.submitted = true;

    if(this.avionForm.invalid){
      return;
    }

    const avion = this.avionForm.value;

    console.log(avion);

  }

    blockNegative(event: KeyboardEvent) {

    if (event.key === '-' || event.key === 'e') {
    event.preventDefault();
    }

  }

  sanitizeNumber(controlName: string) {

    const control = this.avionForm.get(controlName);

    if (!control) return;

    const value = control.value;

    if (value < 0) {
      control.setValue(0);
    } 
  }

}