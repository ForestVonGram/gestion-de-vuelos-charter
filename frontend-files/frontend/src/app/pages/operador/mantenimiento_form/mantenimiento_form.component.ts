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
  // --- Propiedades de Estado y Control ---
  mantenimientoForm: FormGroup;
  isEditMode: boolean = false;
  isViewMode: boolean = false;
  submitted = false;
  loading = false;
  mantenimientoId: number | null = null;

  // Propiedades para la cabecera de usuario
  userName: string = 'Operador';
  userEmail: string = '';
  isDropdownOpen: boolean = false;

  // Opciones para el selector de tipos de mantenimiento (Match con Backend Enum)
  tipos_mantenimiento = [
    { value: 'PREVENTIVO', label: 'Preventivo', icon: '🔧', desc: 'Mantenimiento programado' },
    { value: 'CORRECTIVO', label: 'Correctivo', icon: '⚙️', desc: 'Reparación de fallas' },
    { value: 'REPOSTAJE', label: 'Repostaje', icon: '⛽', desc: 'Carga de combustible' },
    { value: 'INSPECCION', label: 'Inspección', icon: '🔍', desc: 'Inspección técnica' }
  ];

  // Listados auxiliares para poblar los Selects del formulario
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
    // Inicialización del formulario reactivo con validaciones según el DTO de creación
    this.mantenimientoForm = this.fb.group({
      aeronaveId: ['', Validators.required],
      tipo: ['PREVENTIVO', Validators.required],
      descripcion: ['', [Validators.required, Validators.minLength(10)]],
      fechaInicio: [this.getCurrentDateTime()],
      responsableId: [''],
      costo: [''],
      kilometrajeAeronave: [''],
      horasVueloAeronave: [''],
      observaciones: ['']
    });
  }

  ngOnInit(): void {
    this.cargarDatosUsuario();

    // Captura parámetros opcionales de la URL (ej: tipo sugerido)
    this.route.queryParams.subscribe(params => {
      if (params['tipo']) {
        this.mantenimientoForm.patchValue({ tipo: params['tipo'] });
      }
    });

    // Determina si el componente está en modo lectura (ver detalle) o creación
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isViewMode = true;
      this.mantenimientoId = Number(id);
      this.cargarMantenimiento(this.mantenimientoId);
      this.mantenimientoForm.disable(); // Desactiva edición en modo visualización
    }
  }

  // Recupera información de sesión para la UI
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

  // Genera fecha actual compatible con inputs tipo datetime-local
  getCurrentDateTime(): string {
    const now = new Date();
    return now.toISOString().slice(0, 16);
  }

  // Obtiene los datos de un mantenimiento existente para poblar el formulario
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
        console.error('Error al cargar:', error);
        alert('Error al cargar los datos');
        this.loading = false;
        this.router.navigate(['/operador/mantenimiento']);
      }
    });
  }

  // Procesa el envío del formulario al backend
  onSubmit(): void {
    if (this.isViewMode) return;

    this.submitted = true;

    // Validación preventiva antes del envío
    if (this.mantenimientoForm.invalid) {
      this.markFormGroupTouched(this.mantenimientoForm);
      return;
    }

    this.loading = true;

    // Conversión de tipos de datos para coincidir con MantenimientoCreateDTO
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

    // Llamada al servicio para persistir los datos
    this.mantenimientoService.crearMantenimiento(formData).subscribe({
      next: (nuevoMantenimiento) => {
        this.loading = false;
        alert('Mantenimiento creado exitosamente');
        this.router.navigate(['/operador/mantenimiento', nuevoMantenimiento.id]);
      },
      error: (error) => {
        console.error('Error al crear:', error);
        this.loading = false;
        alert(`Error al crear el mantenimiento: ${error.error?.message || 'Error desconocido'}`);
      }
    });
  }

  // Activa visualmente los errores de todos los campos del formulario
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

  // Utilidad para verificar si un campo debe mostrar error visual
  isFieldInvalid(controlName: string): boolean {
    const control = this.mantenimientoForm.get(controlName);
    return control ? (control.touched && control.invalid) : false;
  }

  // Generador de mensajes de error personalizados para el usuario
  getErrorMessage(controlName: string): string {
    const control = this.mantenimientoForm.get(controlName);
    if (!control?.touched) return '';

    if (control.errors?.['required']) return 'Este campo es requerido';
    if (control.errors?.['minlength']) return `Mínimo ${control.errors['minlength'].requiredLength} caracteres`;

    return '';
  }
}
