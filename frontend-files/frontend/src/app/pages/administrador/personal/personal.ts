import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { OnInit, ChangeDetectorRef } from '@angular/core';
import {AdminSidebarComponent} from '../../../shared/admin-sidebar/admin-sidebar.component';
import { AuthService } from '../../../services/auth/auth.service';
import { PersonalService } from '../../../services/personal/personal-service';
import { CargoPersonal } from '../../../models/personal/cargo';
import { EstadoPersonal } from '../../../models/personal/estado-personal';
import Swal from 'sweetalert2';

/**
 * Componente que muestra y gestiona la lista de personal.
 * Permite visualizar, filtrar y administrar los empleados del sistema.
 */
@Component({
  selector: 'app-personal',
  imports: [FormsModule, AdminSidebarComponent],
  templateUrl: './personal.html',
  styleUrl: './personal.css',
})
export class Personal implements OnInit {

  // Usuario actualmente autenticado
  currentUser: any = null;

  // Listas para filtros (obtenidas de los enums)
  cargos: any[] = Object.values(CargoPersonal); // Cargos disponibles
  estados: any[] = Object.values(EstadoPersonal); // Estados disponibles

  // Variables para los filtros
  filtroNombre: string = ''; // Filtro por nombre
  filtroEstado: string = ''; // Filtro por estado
  filtroCargo: string = ''; // Filtro por cargo

  // Lista de personal filtrada a mostrar
  personalFiltrado: any[] = [];

  /**
   * Constructor del componente
   * @param authService servicio de autenticación
   * @param personalService servicio para operaciones con personal
   * @param cdr ChangeDetectorRef para forzar detección de cambios
   */
  constructor(private authService: AuthService, private personalService: PersonalService, private cdr: ChangeDetectorRef) {
    this.currentUser = this.authService.currentUserValue;
    console.log('cargos:', this.cargos);
    console.log('estados:', this.estados);
    console.log(this.currentUser);
  }

  /**
   * Inicialización del componente.
   * Obtiene la lista completa de personal al cargar.
   */
  ngOnInit(): void {
    this.personalService.obtenerPersonal().subscribe({
      next: (response) => {
        this.personalFiltrado = response; // Asignar datos obtenidos
        console.log("Personal obtenido exitosamente:", response);
        this.cdr.detectChanges(); // Forzar actualización de la vista
      },
      error: (error) => {
        console.error('Error al obtener el personal:', error);
      }
    });
  }

  /**
   * Formatea un string reemplazando guiones bajos por espacios.
   * @param valor texto a formatear
   * @returns texto con espacios en lugar de guiones bajos
   */
  formatear(valor: string): string {
    return valor.replace(/_/g, ' ');
  }

  /**
   * Aplica filtros a la lista de personal.
   * Envía los criterios de filtro al servicio y actualiza la vista.
   */
  filtrar(): void {
    this.personalService.filtroPersonal(this.filtroNombre, this.filtroEstado, this.filtroCargo).subscribe({
      next: (response) => {
        this.personalFiltrado = [...response]; // Crear nueva referencia para el array
        this.cdr.detectChanges(); // Forzar actualización de la vista
        console.log("Personal filtrado exitosamente:", response);
      },
      error: (error) => {
        console.error('Error al filtrar:', error);
      }
    });
  }

  /**
   * Obtiene las iniciales de un nombre completo.
   * @param name nombre completo del empleado
   * @returns iniciales en mayúsculas
   */
  getInitials(name: string): string {
    const names = name.split(' ');
    const initials = names.map(n => n.charAt(0).toUpperCase()).join('');
    return initials;
  }

  /**
   * Formatea una fecha para mostrarla de forma legible.
   * @param dateString fecha en formato string
   * @returns fecha formateada (ej: "1 de enero de 2025")
   */
  formatDate(dateString: string): string {
    const options: Intl.DateTimeFormatOptions = { year: 'numeric', month: 'long', day: 'numeric' };
    return new Date(dateString).toLocaleDateString(undefined, options);
  }

  onDelete(id: number): void {
    Swal.fire({
      title: '¿Estás seguro?',
      text: 'Esta acción no se puede deshacer.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.personalService.eliminarPersonal(id).subscribe({
          next: (response) => {
            console.log("Personal eliminado exitosamente:", response);
            this.personalFiltrado = this.personalFiltrado.filter(p => p.id !== id);
            this.cdr.detectChanges();
            Swal.fire({
              icon: 'success',
              title: '¡Eliminado!',
              text: 'El personal ha sido eliminado exitosamente.',
              confirmButtonText: 'Aceptar'
            });
          },
          error: (error) => {
            console.error("Error al eliminar personal:", error);
            Swal.fire({
              icon: 'error',
              title: '¡Error!',
              text: 'Ocurrió un error al eliminar el personal. Por favor, inténtelo de nuevo.',
              confirmButtonText: 'Aceptar'
            });
          }
        });
      }
    });
  }

  onActivate(id: number) {
    this.personalService.activarPersonal(id).subscribe({
      next: (response) => {
        Swal.fire({
          icon: 'success',
          title: '¡Activado!',
          text: 'El personal ha sido activado exitosamente.',
          confirmButtonText: 'Aceptar'
        });
        this.filtrar();
        this.cdr.detectChanges();
      },
      error: (error) => {
        Swal.fire({
          icon: 'error',
          title: '¡Error!', 
          text: 'Ocurrió un error al activar el personal. Por favor, inténtelo de nuevo.',
          confirmButtonText: 'Aceptar'
        });
      }
    });
  }

}
