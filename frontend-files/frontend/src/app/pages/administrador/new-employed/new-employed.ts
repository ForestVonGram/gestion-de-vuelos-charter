import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CargoPersonal } from '../../../models/personal/cargo';
import { AdminSidebarComponent } from '../../../shared/admin-sidebar/admin-sidebar.component';
import { AuthService } from '../../../services/auth/auth.service';

@Component({
  selector: 'app-new-employed',
  imports: [ReactiveFormsModule, AdminSidebarComponent],
  templateUrl: './new-employed.html',
  styleUrl: './new-employed.css',
})
export class NewEmployed {
  personalForm!: FormGroup;

  cargos = Object.values(CargoPersonal);

  currentUser : any = null; 
  submitted = false; 

  constructor(private fb: FormBuilder, private authService: AuthService) {
    this.currentUser = this.authService.currentUserValue;
  }

  ngOnInit(): void {

    this.personalForm = this.fb.group({

      usuarioId: [null, Validators.required],

      numeroEmpleado: ['', Validators.required],

      cargo: [null, Validators.required],

      areaEspecializacion: [''],

      certificaciones: [''],

      fechaContratacion: [''],

      turno: [''],

      observaciones: ['']

    });

  }

  guardarPersonal() {

    if (this.personalForm.invalid) {
      this.personalForm.markAllAsTouched();
      return;
    }

    const datos = this.personalForm.value;

    console.log("Datos enviados:", datos);

    // aquí llamarías al servicio
    // this.personalService.crear(datos).subscribe()

  }

  campoInvalido(campo: string): boolean {
    const control = this.personalForm.get(campo);
    return !!(control && control.invalid && control.touched);
  }

}

