import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CargoPersonal } from '../../../models/personal/cargo';
import { AdminSidebarComponent } from '../../../shared/admin-sidebar/admin-sidebar.component';
import { AuthService } from '../../../services/auth/auth.service';
import { PersonalService } from '../../../services/personal/personal-service';
import Swal from 'sweetalert2';

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

  constructor(private fb: FormBuilder, private authService: AuthService, private personalService: PersonalService ) {
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
    this.personalService.crearPersonal(datos).subscribe({
      next: (response) => {
        console.log("Personal creado exitosamente:", response);
        this.personalForm.reset();
        this.submitted = false;
        Swal.fire({
          icon: 'success',
          title: '¡Éxito!',
          text: 'El personal ha sido creado exitosamente.',
          confirmButtonText: 'Aceptar'
        });
      },
      error: (error) => {
        console.error("Error al crear personal:", error);
        Swal.fire({
          icon: 'error',
          title: '¡Error!',
          text: 'Ocurrió un error al crear el personal. Por favor, inténtelo de nuevo.',
          confirmButtonText: 'Aceptar'
        });
      }
    });
    
  }

  campoInvalido(campo: string): boolean {
    const control = this.personalForm.get(campo);
    return !!(control && control.invalid && control.touched);
  }

  blockNegative(event: KeyboardEvent) {
    if (event.key === '-' || event.key === 'e' || event.key === '+' || event.key === '.') {
    event.preventDefault();
    }
  }

  sanitizeNumber(controlName: string) {
    const control = this.personalForm.get(controlName);
    if (!control) return;

    let value = Number(control.value);
    if (isNaN(value) || value < 0) {
    control.setValue(0);
    }
  }

}

