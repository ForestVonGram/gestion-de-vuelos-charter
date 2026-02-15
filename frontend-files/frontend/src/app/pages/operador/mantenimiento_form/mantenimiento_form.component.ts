import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-mantenimiento-form',
  templateUrl: './mantenimiento_form.component.html',
  styleUrls: ['./mantenimiento_form.component.css'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule]
})
export class MantenimientoFormComponent implements OnInit {
  mantenimientoForm: FormGroup;
  isEditMode: boolean = false;
  submitted = false;

  tipos_mantenimiento = [
    { value: 'PREVENTIVO', label: 'Preventivo', icon: '🔧' },
    { value: 'CORRECTIVO', label: 'Correctivo', icon: '⚙️' },
    { value: 'PREDICTIVO', label: 'Predictivo', icon: '📊' }
  ];

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.mantenimientoForm = this.fb.group({
      // Información de la aeronave
      aeronave_matricula: ['', [Validators.required, Validators.pattern('^[A-Z]{2}-[A-Z0-9]{3}$')]],

      // Tipo y descripción
      tipo: ['PREVENTIVO', Validators.required],
      descripcion: ['', [Validators.required, Validators.minLength(10)]],

      // Fechas
      fecha_inicio: [this.getCurrentDateTime(), Validators.required],
      fecha_fin: [''],

      // Responsable
      responsable_nombre: ['', Validators.required],

      // Costos
      costo: ['', [Validators.required, Validators.min(0)]],

      // Datos técnicos
      kilometraje_aeronave: ['', [Validators.required, Validators.min(0)]],
      horas_vuelo_aeronave: ['', [Validators.required, Validators.min(0)]],

      // Observaciones
      observaciones: [''],

      // Estado
      completado: [false]
    });
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      // Aquí cargarías los datos del mantenimiento
      this.cargarDatosPrueba();
    }
  }

  getCurrentDateTime(): string {
    const now = new Date();
    return now.toISOString().slice(0, 16);
  }

  cargarDatosPrueba() {
    // Datos de ejemplo para edición
    this.mantenimientoForm.patchValue({
      aeronave_matricula: 'LV-ABC',
      tipo: 'PREVENTIVO',
      descripcion: 'Inspección anual de motor y sistemas',
      fecha_inicio: '2024-02-15T08:00',
      fecha_fin: '',
      responsable_nombre: 'Carlos Rodríguez',
      costo: 2500,
      kilometraje_aeronave: 15000,
      horas_vuelo_aeronave: 1200,
      observaciones: 'Cambio de aceite y filtros programado',
      completado: false
    });
  }

  onSubmit(): void {
    this.submitted = true;

    if (this.mantenimientoForm.invalid) {
      this.markFormGroupTouched(this.mantenimientoForm);
      return;
    }

    console.log('Datos del formulario:', this.mantenimientoForm.value);

    // Aquí iría la llamada al servicio
    if (this.isEditMode) {
      alert('Mantenimiento actualizado exitosamente');
    } else {
      alert('Mantenimiento creado exitosamente');
    }

    this.router.navigate(['/mantenimientos']);
  }

  markFormGroupTouched(formGroup: FormGroup) {
    Object.values(formGroup.controls).forEach(control => {
      control.markAsTouched();
      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      }
    });
  }

  cancelar(): void {
    if (this.isEditMode) {
      const id = this.route.snapshot.paramMap.get('id');
      this.router.navigate(['/mantenimientos', id]);
    } else {
      this.router.navigate(['/mantenimientos']);
    }
  }

  isFieldInvalid(controlName: string): boolean {
    const control = this.mantenimientoForm.get(controlName);
    return control ? (control.touched && control.invalid) : false;
  }

  getErrorMessage(controlName: string): string {
    const control = this.mantenimientoForm.get(controlName);
    if (!control?.touched) return '';

    if (control.errors?.['required']) return 'Este campo es requerido';
    if (control.errors?.['minlength']) return `Mínimo ${control.errors['minlength'].requiredLength} caracteres`;
    if (control.errors?.['min']) return 'El valor debe ser mayor a 0';
    if (control.errors?.['pattern']) return 'Formato inválido (ej: LV-ABC)';

    return '';
  }
}
