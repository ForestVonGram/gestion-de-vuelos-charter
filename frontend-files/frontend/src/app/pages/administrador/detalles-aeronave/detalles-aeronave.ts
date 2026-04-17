import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router, ActivatedRoute } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { AuthService, User } from '../../../services/auth/auth.service';
import { AdminSidebarComponent } from '../../../shared/admin-sidebar/admin-sidebar.component';
import { AccesibilidadComponent } from '../../../shared/accesibilidad/accesibilidad.component';
import { EstadoAeronave } from '../../../models/avion/estado-avion';
import { AeronaveDTO } from '../../../models/avion/avion';
import { Aeronave } from '../../../services/vuelos/aeronave_service';
import { ChangeDetectorRef } from '@angular/core';
import { FormsModule } from '@angular/forms';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-detalles-aeronave',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule, AdminSidebarComponent, AccesibilidadComponent, FormsModule],
  templateUrl: './detalles-aeronave.html',
  styleUrl: './detalles-aeronave.css'
})
export class DetallesAeronave implements OnInit {

  currentUser: User | null = null;
  aeronave!: AeronaveDTO ;
  estados = Object.values(EstadoAeronave);
  mostrarModalEliminar = false;
  imagenesPreview: { url: string; nombre: string; file: File }[] = [];
  archivosSeleccionados: File[] = [];
  horasAIncrementar: number = 0;

  editForm: FormGroup;

  constructor(
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute,
    private fb: FormBuilder,
    private aeronaveService: Aeronave,
    private cdr: ChangeDetectorRef
  ) {
    this.editForm = this.fb.group({
      estado: [''],
      horasVueloTotales: [0],
      fechaUltimaRevision: [''],
      especificacionesTecnicas: ['']
    });
  }

  ngOnInit(): void {
    this.currentUser = this.authService.currentUserValue;
    const id = this.route.snapshot.paramMap.get('id');
    if (id) this.cargarAeronave(Number(id));
  }

  cargarAeronave(id: number): void {
    this.aeronaveService.getAeronaveById(id).subscribe({
      next: (data) => {
        this.aeronave = data;
        this.editForm.patchValue({
          estado: data.estado,
          horasVueloTotales: data.horasVueloTotales,
          fechaUltimaRevision: data.fechaUltimaRevision,
          especificacionesTecnicas: data.especificacionesTecnicas
        });
        this.cdr.detectChanges(); // Actualizar la vista con los datos obtenidos
      },
      error: (e) => console.error('Error cargando aeronave:', e)
    });
  }

  guardarCambios(): void {
    if (this.editForm.invalid || !this.aeronave) return;
    this.aeronaveService.actualizarAeronave(this.aeronave.id, this.editForm.value).subscribe({
      next: () => {
        console.log(this.editForm.value);
        Swal.fire('Éxito', 'La aeronave ha sido actualizada correctamente.', 'success');
        this.cargarAeronave(this.aeronave!.id); // Recargar para mostrar cambios
      },
      error: (e) => {
        console.error('Error actualizando aeronave:', e);
        Swal.fire('Error', 'Hubo un problema al actualizar la aeronave.', 'error');
      }

    });
  }

  confirmarEliminar(): void {
    this.mostrarModalEliminar = true;
  }

  eliminarAeronave(): void {
    if (!this.aeronave) return;
    this.aeronaveService.eliminarAeronave(this.aeronave.id).subscribe({
      next: () => this.router.navigate(['/admin/flota']),
      error: (e) => console.error('Error eliminando:', e)
    });
  }

  onFileSelected(event: Event): void {
    const files = (event.target as HTMLInputElement).files;
    if (!files) return;
    this.procesarArchivos(Array.from(files));
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    const files = event.dataTransfer?.files;
    if (!files) return;
    this.procesarArchivos(Array.from(files));
  }

  procesarArchivos(files: File[]): void {
    files.forEach(file => {
      const reader = new FileReader();
      reader.onload = (e) => {
        this.imagenesPreview.push({
          url: e.target?.result as string,
          nombre: file.name,
          file
        });
      };
      reader.readAsDataURL(file);
      this.archivosSeleccionados.push(file);
    });
  }

  quitarPreview(index: number): void {
    this.imagenesPreview.splice(index, 1);
    this.archivosSeleccionados.splice(index, 1);
  }

  subirImagenes(): void {
    if (!this.aeronave) return;
    const formData = new FormData();
  
    this.archivosSeleccionados.forEach(f => formData.append('files', f)); // 'files' no 'imagenes'
    formData.append('tipo', 'EXTERIOR'); //  requerido por el backend

    this.aeronaveService.subirImagenes(this.aeronave.id, formData).subscribe({
      next: () => {
        this.imagenesPreview = [];
        this.archivosSeleccionados = [];
        this.cargarAeronave(this.aeronave!.id);
      },
      error: (e) => console.error('Error subiendo imágenes:', e)
    });
  }

  eliminarImagen(imagenId: number): void {
    if (!this.aeronave) return;
    this.aeronaveService.eliminarImagen(this.aeronave.id, imagenId).subscribe({
      next: () => this.cargarAeronave(this.aeronave!.id),
      error: (e) => console.error('Error eliminando imagen:', e)
    });
  }

  formatearEstado(estado: string): string {
    return estado
      .toLowerCase()
      .replace(/_/g, ' ') 
      .replace(/\b\w/g, letra => letra.toUpperCase());
  }

incrementarHoras() {
  if (!this.horasAIncrementar || this.horasAIncrementar <= 0) return;

  this.aeronaveService.incrementarHorasVuelo(this.aeronave.id, this.horasAIncrementar)
    .subscribe({
      next: () => {
        this.aeronave.horasVueloTotales += this.horasAIncrementar;
        this.horasAIncrementar = 0;
        this.cdr.detectChanges(); // Actualizar la vista con el nuevo total de horas
      },
      error: (err) => {
        console.error(err);
      }
    });
}
}