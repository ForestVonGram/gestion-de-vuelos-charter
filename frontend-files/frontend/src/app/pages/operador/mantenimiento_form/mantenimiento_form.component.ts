import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { MantenimientoService, Mantenimiento } from '../../../services/operador/mantenimiento/mantenimiento.service';
import { AuthService } from '../../../services/auth/auth.service';

@Component({
  selector: 'app-mantenimiento-form',
  templateUrl: './mantenimiento_form.component.html',
  styleUrls: ['./mantenimiento_form.component.css'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule]
})
export class MantenimientoFormComponent implements OnInit {
  mantenimientoForm: FormGroup;
  isEditMode: boolean = false;
  isViewMode: boolean = false;
  submitted = false;
  loading = false;
  mantenimientoId: number | null = null;

  // Propiedades para el dropdown de usuario
  userName: string = 'Operador';
  userEmail: string = '';
  isDropdownOpen: boolean = false;

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
    private mantenimientoService: MantenimientoService,
    private authService: AuthService
  ) {
    this.mantenimientoForm = this.fb.group({
      aeronaveId: ['', Validators.required],           // @NotNull
      tipo: ['PREVENTIVO', Validators.required],       // @NotNull
      descripcion: ['', [Validators.required, Validators.minLength(10)]], // @NotBlank
      fechaInicio: [this.getCurrentDateTime()],        // Opcional en DTO
      responsableId: [''],                              // Opcional en DTO
      costo: [''],                                      // Opcional en DTO
      kilometrajeAeronave: [''],                        // Opcional en DTO
      horasVueloAeronave: [''],                         // Opcional en DTO
      observaciones: ['']                               // Opcional en DTO
      // 👆 ELIMINADO: completado - No existe en MantenimientoCreateDTO
    });
  }

  ngOnInit(): void {
    this.cargarDatosUsuario();

    // Verificar si hay tipo en query params (viene de mantenimientos ofrecidos)
    this.route.queryParams.subscribe(params => {
      if (params['tipo']) {
        this.mantenimientoForm.patchValue({
          tipo: params['tipo']
        });
      }
    });

    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isViewMode = true;
      this.mantenimientoId = Number(id);
      this.cargarMantenimiento(this.mantenimientoId);
      this.mantenimientoForm.disable();
    }
  }

  cargarDatosUsuario(): void {
    const currentUser = this.authService.currentUserValue;
    if (currentUser) {
      this.userName = currentUser.nombreCompleto || 'Operador';
      this.userEmail = currentUser.email || '';
    }
  }

  toggleDropdown(): void {
    this.isDropdownOpen = !this.isDropdownOpen;
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/auth/login']);
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
          observaciones: data.observaciones
        });
        this.loading = false;
      },
      error: (error) => {
        console.error('Error al cargar mantenimiento:', error);
        alert('Error al cargar los datos del mantenimiento');
        this.loading = false;
        this.router.navigate(['/operador/mantenimiento']);
      }
    });
  }

  onSubmit(): void {
    if (this.isViewMode) {
      return;
    }

    this.submitted = true;

    if (this.mantenimientoForm.invalid) {
      this.markFormGroupTouched(this.mantenimientoForm);

      // Mostrar qué campos están inválidos para depuración
      Object.keys(this.mantenimientoForm.controls).forEach(key => {
        const control = this.mantenimientoForm.get(key);
        if (control?.invalid) {
          console.log(`Campo inválido: ${key}`, control.errors);
        }
      });

      return;
    }

    this.loading = true;

    // Preparar los datos exactamente como los espera el backend (MantenimientoCreateDTO)
    const formData = {
      aeronaveId: Number(this.mantenimientoForm.value.aeronaveId),
      tipo: this.mantenimientoForm.value.tipo,
      descripcion: this.mantenimientoForm.value.descripcion,
      fechaInicio: this.mantenimientoForm.value.fechaInicio ? new Date(this.mantenimientoForm.value.fechaInicio).toISOString() : null,
      responsableId: this.mantenimientoForm.value.responsableId ? Number(this.mantenimientoForm.value.responsableId) : null,
      costo: this.mantenimientoForm.value.costo ? Number(this.mantenimientoForm.value.costo) : null,
      kilometrajeAeronave: this.mantenimientoForm.value.kilometrajeAeronave ? Number(this.mantenimientoForm.value.kilometrajeAeronave) : null,
      horasVueloAeronave: this.mantenimientoForm.value.horasVueloAeronave ? Number(this.mantenimientoForm.value.horasVueloAeronave) : null,
      observaciones: this.mantenimientoForm.value.observaciones || null
    };

    console.log('Enviando datos al backend:', formData);

    this.mantenimientoService.crearMantenimiento(formData).subscribe({
      next: (nuevoMantenimiento) => {
        this.loading = false;
        alert('Mantenimiento creado exitosamente');
        this.router.navigate(['/operador/mantenimiento', nuevoMantenimiento.id]);
      },
      error: (error) => {
        console.error('Error al crear:', error);
        this.loading = false;

        // Mostrar mensaje de error más detallado
        if (error.error) {
          console.error('Detalles del error:', error.error);
          alert(`Error al crear el mantenimiento: ${error.error.message || 'Error desconocido'}`);
        } else {
          alert('Error al crear el mantenimiento');
        }
      }
    });
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
    this.router.navigate(['/operador/mantenimiento']);
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

    return '';
  }
}
