import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AdminSidebarComponent } from '../../../../shared/admin-sidebar/admin-sidebar.component';
import { NominaService } from '../../../../services/personal/nomina-service';
import { AuthService } from '../../../../services/auth/auth.service';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { EstadoNomina } from '../../../../models/personal/estado-nomina';
import { CommonModule } from '@angular/common';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-edit-nomina',
  imports: [AdminSidebarComponent,ReactiveFormsModule, CommonModule],
  templateUrl: './edit-nomina.html',
  styleUrl: './edit-nomina.css',
})
export class EditNomina implements OnInit {

  currentUser!: any;
  nominaSeleccionada!: any;
  nominaUpdateForm!: FormGroup;
  estados = Object.values(EstadoNomina);

  constructor(
    private fb: FormBuilder,
    private nominaService: NominaService,
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.currentUser = this.authService.currentUserValue;
  }

  ngOnInit(): void {
    // 1. Inicializar el form PRIMERO
    this.nominaUpdateForm = this.fb.group({
      bonificaciones:      [null, [Validators.min(0)]],
      deducciones:         [null, [Validators.min(0)]],
      descuentoImpuesto:   [null, [Validators.min(0)]],
      descuentoAfiliacion: [null, [Validators.min(0)]],
      estado:              ['', Validators.required],
      observaciones:       ['']
    });

    // 2. Leer el ID de la ruta y cargar la nómina
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.nominaService.obtenerNominaPorId(+id).subscribe({
        next: (data) => {
          this.nominaSeleccionada = data;
          this.nominaUpdateForm.patchValue({
            bonificaciones: data.bonificaciones,
            deducciones: data.deducciones,
            descuentoImpuesto: data.descuentoImpuesto,
            descuentoAfiliacion: data.descuentoAfiliacion,
            estado: data.estado,
            observaciones: data.observaciones
          });
        },
        error: () => {
          Swal.fire('Error', 'No se pudo cargar la nómina.', 'error');
          this.router.navigate(['/admin/nomina']);
        }
      });
    } 
  }

  get f() {
    return this.nominaUpdateForm.controls;
  }

  onReset(): void {
    this.router.navigate(['/admin/nomina']);
  }

  onUpdate(): void {
    if (this.nominaUpdateForm.invalid) return;

    const payload = this.nominaUpdateForm.value;

    this.nominaService.actualizarNomina(this.nominaSeleccionada.id, payload).subscribe({
      next: () => {
        Swal.fire('Éxito', 'La nómina ha sido actualizada correctamente.', 'success')
      },
      error: () => {
        Swal.fire('Error', 'Hubo un problema al actualizar la nómina.', 'error');
      }
    });
  }
}