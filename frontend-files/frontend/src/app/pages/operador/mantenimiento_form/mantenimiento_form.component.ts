import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MantenimientoService, Mantenimiento } from '../../../services/operador/mantenimiento/mantenimiento.service';

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
  loading = false;
  mantenimientoId: number | null = null;

  // Tipos de mantenimiento según el enum del backend
  tipos_mantenimiento = [
    { value: 'PREVENTIVO', label: 'Preventivo', icon: '🔧', desc: 'Mantenimiento programado' },
    { value: 'CORRECTIVO', label: 'Correctivo', icon: '⚙️', desc: 'Reparación de fallas' },
    { value: 'REPOSTAJE', label: 'Repostaje', icon: '⛽', desc: 'Carga de combustible' },
    { value: 'INSPECCION', label: 'Inspección', icon: '🔍', desc: 'Inspección técnica' }
  ];

  // Datos de ejemplo para selects (esto vendría del backend)
  aeronaves = [
    { id: 101, matricula: 'LV-ABC' },
    { id: 102, matricula: 'LV-XYZ' },
    { id: 103, matricula: 'LV-DEF' }
  ];

  responsables = [
    { id: 1, nombre: 'Carlos Rodríguez' },
    { id: 2, nombre: 'María González' },
    { id: 3, nombre: 'Juan Pérez' },
    { id: 4, nombre: 'Ana Martínez' },
    { id: 5, nombre: 'Luis Sánchez' }
  ];

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private mantenimientoService: MantenimientoService
  ) {
    this.mantenimientoForm = this.fb.group({
      // Según MantenimientoCreateDTO
      aeronaveId: ['', Validators.required],
      tipo: ['PREVENTIVO', Validators.required],
      descripcion: ['', [Validators.required, Validators.minLength(10)]],
      fechaInicio: [this.getCurrentDateTime(), Validators.required],
      responsableId: ['', Validators.required],
      costo: ['', [Validators.required, Validators.min(0)]],
      kilometrajeAeronave: ['', [Validators.required, Validators.min(0)]],
      horasVueloAeronave: ['', [Validators.required, Validators.min(0)]],
      observaciones: [''],

      // Campo adicional para el DTO de respuesta (solo en edición)
      completado: [false]
    });
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.mantenimientoId = Number(id);
      this.cargarMantenimiento(this.mantenimientoId);
    }
  }

  getCurrentDateTime(): string {
    const now = new Date();
    return now.toISOString().slice(0, 16);
  }

  cargarMantenimiento(id: number): void {
    this.loading = true;
    this.mantenimientoService.obtenerPorId(id).subscribe({
      next: (data) => {
        this.mantenimientoForm.patchValue({
          aeronaveId: data.aeronaveId,
          tipo: data.tipo,
          descripcion: data.descripcion,
          fechaInicio: data.fechaInicio.slice(0, 16),
          responsableId: data.responsableId,
          costo: data.costo,
          kilometrajeAeronave: data.kilometrajeAeronave,
          horasVueloAeronave: data.horasVueloAeronave,
          observaciones: data.observaciones,
          completado: data.completado
        });
        this.loading = false;
      },
      error: (error) => {
        console.error('Error al cargar mantenimiento:', error);
        alert('Error al cargar los datos del mantenimiento');
        this.loading = false;
        this.router.navigate(['/mantenimientos']);
      }
    });
  }

  onSubmit(): void {
    this.submitted = true;

    if (this.mantenimientoForm.invalid) {
      this.markFormGroupTouched(this.mantenimientoForm);
      return;
    }

    this.loading = true;
    const formData = this.mantenimientoForm.value;

    if (this.isEditMode && this.mantenimientoId) {
      // Actualizar mantenimiento existente
      this.mantenimientoService.actualizarMantenimiento(this.mantenimientoId, formData).subscribe({
        next: () => {
          this.loading = false;
          alert('Mantenimiento actualizado exitosamente');
          this.router.navigate(['/mantenimientos', this.mantenimientoId]);
        },
        error: (error) => {
          console.error('Error al actualizar:', error);
          this.loading = false;
          alert('Error al actualizar el mantenimiento');
        }
      });
    } else {
      // Crear nuevo mantenimiento
      this.mantenimientoService.crearMantenimiento(formData).subscribe({
        next: (nuevoMantenimiento) => {
          this.loading = false;
          alert('Mantenimiento creado exitosamente');
          this.router.navigate(['/mantenimientos', nuevoMantenimiento.id]);
        },
        error: (error) => {
          console.error('Error al crear:', error);
          this.loading = false;
          alert('Error al crear el mantenimiento');
        }
      });
    }
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
    if (this.isEditMode && this.mantenimientoId) {
      this.router.navigate(['/mantenimientos', this.mantenimientoId]);
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

    return '';
  }

  getAeronaveMatricula(aeronaveId: number): string {
    const aeronave = this.aeronaves.find(a => a.id === aeronaveId);
    return aeronave ? aeronave.matricula : '';
  }
}
