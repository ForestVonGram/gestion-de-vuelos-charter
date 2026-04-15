import { Component } from '@angular/core';
import { CreateTripulanteDto } from '../../../models/personal/create-tripulante-dto';
import { AuthService } from '../../../services/auth/auth.service';
import { FormBuilder, FormGroup } from '@angular/forms';
import { AdminSidebarComponent } from '../../../shared/admin-sidebar/admin-sidebar.component';
import { ReactiveFormsModule } from '@angular/forms';
import { Tripulante } from '../../../services/personal/tripulante';
import { AccesibilidadComponent } from '../../../shared/accesibilidad/accesibilidad.component';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-nuevo-tripulante',
  imports: [AdminSidebarComponent, ReactiveFormsModule, AccesibilidadComponent],
  templateUrl: './nuevo-tripulante.html',
  styleUrl: './nuevo-tripulante.css',
})
export class NuevoTripulante {

  currentUser!:any;
  form!: FormGroup;
  submitted = false;

  constructor( private authService: AuthService,
              private fb: FormBuilder, private tripulanteService: Tripulante) {
    this.currentUser = this.authService.currentUserValue;
  }

  ngOnInit(): void {
    this.form = this.fb.group({
      usuarioId: [, []],
      numeroLicencia: ['', []],
      tipoLicencia: ['', []],
      fechaExpedicionLicencia: ['', []],
      fechaVencimientoLicencia: ['', []],
      esPiloto: [false, []],
      certificaciones: ['', []],
      observaciones: ['', []]
    });
  }

  guardarTripulante(): void {
  this.tripulanteService.registrarTripulante(this.form.value).subscribe({
    next: (response) => {
      console.log('Tripulante registrado:', response);
      Swal.fire({
        icon: 'success',
        title: '¡Éxito!',
        text: 'El tripulante ha sido registrado exitosamente.',
      });
      this.form.reset();
    },
    error: (error: any) => {
      console.error('Error al registrar tripulante:', error);
      Swal.fire({
        icon: 'error',
        title: 'Error',
        text: 'Ocurrió un error al registrar el tripulante.',
      });
    }
    });
  }

  cancelar(): void {
    this.form.reset();
  }
}