import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { FormGroup, FormsModule, Validators, FormBuilder } from '@angular/forms';
import { AdminSidebarComponent } from '../../../../shared/admin-sidebar/admin-sidebar.component';
import { AuthService } from '../../../../services/auth/auth.service';
import { PersonalService } from '../../../../services/personal/personal-service';
import { ReactiveFormsModule } from '@angular/forms';
import { EstadoNomina } from '../../../../models/personal/estado-nomina';
import Swal from 'sweetalert2';
import { AccesibilidadComponent } from '../../../../shared/accesibilidad/accesibilidad.component';
import { NominaService } from '../../../../services/personal/nomina-service';

@Component({
  selector: 'app-create-roster',
  imports: [AdminSidebarComponent, AccesibilidadComponent,FormsModule, RouterModule, ReactiveFormsModule],
  templateUrl: './create-roster.html',
  styleUrl: './create-roster.css',
})
export class CreateRoster {
  currentUser: any = null;
  nominaForm!: FormGroup;
  personal: any[] = [];
  personalSeleccionado: any = null;


    meses = [
    { value: 1,  label: 'Enero' },
    { value: 2,  label: 'Febrero' },
    { value: 3,  label: 'Marzo' },
    { value: 4,  label: 'Abril' },
    { value: 5,  label: 'Mayo' },
    { value: 6,  label: 'Junio' },
    { value: 7,  label: 'Julio' },
    { value: 8,  label: 'Agosto' },
    { value: 9,  label: 'Septiembre' },
    { value: 10, label: 'Octubre' },
    { value: 11, label: 'Noviembre' },
    { value: 12, label: 'Diciembre' },
  ];
  estados = Object.values(EstadoNomina);
  constructor(private fb: FormBuilder, private authService: AuthService,
     private personalService: PersonalService, private nominaService: NominaService) {
    this.currentUser = this.authService.currentUserValue;
    this.cargarPersonal();
  }
    ngOnInit(): void {
    this.nominaForm = this.fb.group({
      personalId: [null, Validators.required],
      usuarioNombre: [{ value: '', disabled: true }],
      usuarioApellido: [{ value: '', disabled: true }],
      mes:                 ['', Validators.required],
      ano:                 [new Date().getFullYear(), [Validators.required, Validators.min(2000)]],
      salarioBase:         [0, [Validators.required, Validators.min(0)]],
      estado:              ['PENDIENTE', Validators.required],
      bonificaciones:      [0, [Validators.min(0)]],
      deducciones:        [0, [Validators.min(0)]],
      descuentoImpuesto:  [0, [Validators.min(0)]],
      descuentoAfiliacion:[0, [Validators.min(0)]],
      totalNeto:           [{ value: 0, disabled: true }],
      observaciones:       [''],
    });
  }

  cargarPersonal() {
    this.personalService.obtenerPersonal().subscribe({
      next: (response) => {
        this.personal = response;
      }
    });
  }

generateRoster(): void {
  const raw = this.nominaForm.getRawValue();

  const data = {
    ...raw,
    mes: Number(raw.mes),
    ano: Number(raw.ano),
    salarioBase: Number(raw.salarioBase),
    bonificaciones: raw.bonificaciones ?? 0,
    deducciones: raw.deducciones ?? 0,
    descuentoImpuesto: raw.descuentoImpuesto ?? 0,
    descuentoAfiliacion: raw.descuentoAfiliacion ?? 0,
  };

  this.nominaService.calcularNomina(data).subscribe({
    next: (response) => {
      //actualizar el total neto en el formulario
      this.nominaForm.patchValue({
        totalNeto: response.totalNeto
      });

      Swal.fire('Éxito', 'Nómina generada correctamente.', 'success');
      console.log('Respuesta:', response);
      },
    error: (error) => {
      console.error(error);
      Swal.fire('Error', 'Hubo un problema al generar la nómina.', 'error');
      }
    });
  }
  

  // Getter para acceder fácilmente a los controles en el HTML
  get f() {
    return this.nominaForm.controls;
  }

  onSubmit(): void {
    if (this.nominaForm.invalid) {
      Swal.fire('Error', 'Por favor corrige los errores en el formulario.', 'error');
      return;
    }
  }
  onReset(): void {
    this.nominaForm.reset({
      mes: new Date().getMonth() + 1,
      ano: new Date().getFullYear(),
      estado: 'PENDIENTE',
      totalNeto: 0
    });
  }

  onPersonalChange(event: any): void {
    const personalId = event.target.value;
    this.personalSeleccionado = this.personal.find((p: any) => p.id === +personalId);
    if (this.personalSeleccionado) {
      this.nominaForm.patchValue({
        personalNombre: this.personalSeleccionado.nombre,
        personalApellido: this.personalSeleccionado.apellido
      });
      console.log('Personal seleccionado:', this.personalSeleccionado);
    }
  }

  



}
