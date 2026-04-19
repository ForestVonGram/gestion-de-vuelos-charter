import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService, User } from '../../../services/auth/auth.service';
import { AdminSidebarComponent } from '../../../shared/admin-sidebar/admin-sidebar.component';
import {AccesibilidadComponent} from '../../../shared/accesibilidad/accesibilidad.component';
import { VueloDTO } from '../../../models/Vuelos/vuelo-dto';
import { EstadoVuelo } from '../../../models/Vuelos/estado-vuleo';
import { VuelosService } from '../../../services/vuelos/vuelos.service';
import { ChangeDetectorRef } from '@angular/core';
import Swal from 'sweetalert2';


/**
 * Componente que muestra y gestiona los vuelos para administradores.
 * Presenta una tabla con todos los vuelos y su información relevante.
 */
@Component({
  selector: 'app-vuelos-admin',
  standalone: true,
  imports: [CommonModule, RouterModule, AdminSidebarComponent, AccesibilidadComponent],
  templateUrl: './vuelos-admin.component.html',
  styleUrls: ['./vuelos-admin.component.css']
})
export class VuelosAdminComponent implements OnInit {

  // Usuario actualmente autenticado
  currentUser: User | null = null;

  // Lista de vuelos a mostrar en la tabla
  vuelos: VueloDTO[] = [];

  /**
   * Constructor del componente
   * @param authService servicio de autenticación
   * @param router servicio de navegación
   */
  constructor(private authService: AuthService, private router: Router,
    private vuelosService: VuelosService, private cdr: ChangeDetectorRef) {

    }

  /**
   * Inicialización del componente.
   * Obtiene el usuario actual y carga los datos simulados.
   */
  ngOnInit(): void {
    this.currentUser = this.authService.currentUserValue;
    this.obtnerVuelos();
  }

  /**
   * Cierra la sesión del usuario actual y redirige al login.
   */
  logout(): void {
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }

getEstadoVueloClass(estado: EstadoVuelo): string {
  switch (estado) {
    case EstadoVuelo.SOLICITADO: return 'status-pending';
    case EstadoVuelo.CONFIRMADO: return 'status-approved';
    case EstadoVuelo.COMPLETADO: return 'status-completed';
    case EstadoVuelo.CANCELADO: return 'status-cancelled';
    case EstadoVuelo.EN_CURSO: return 'status-error';
    default: return '';
  }
}

  obtnerVuelos(): void {
    this.vuelosService.obtenerVuelosSolicitados().subscribe({
      next: (data) => {
        this.vuelos = data;
        this.cdr.detectChanges(); // Actualizar la vista con los datos obtenidos
      },
      error: (error) => {
        console.error('Error al obtener vuelos:', error);
        Swal.fire('Error', 'No se pudieron cargar los vuelos. Intente nuevamente más tarde.', 'error');
      }
    });
  }

  cancelarVuelo(id: number) {
    Swal.fire({
      title: 'Motivo del rechazo',
      input: 'text',
      inputLabel: 'Escribe el motivo',
      inputPlaceholder: 'Ej: Documentación incompleta',
      showCancelButton: true,
      confirmButtonText: 'Rechazar',
      cancelButtonText: 'Cancelar',
      inputValidator: (value) => {
        if (!value) {
          return 'Debes escribir un motivo';
        }
        return null;
      }
    }).then(result => {

      if (result.isConfirmed) {
        const motivo = result.value;

        this.vuelosService.RechazarSolicitudVuelo(id, { motivo }).subscribe({
          next: () => {
            Swal.fire('Rechazado', 'El vuelo fue rechazado correctamente', 'success');
            this.obtnerVuelos(); // Refresca la lista de vuelos después de rechazar
            this.cdr.detectChanges(); // Asegura que la vista se actualice con los nuevos datos
          },
          error: () => {
            Swal.fire('Error', 'No se pudo rechazar el vuelo', 'error');
          }
        });
    }
      });
  }
  aceptarSolicitud(id: number): void {
    Swal.fire({
      title: '¿Aprobar vuelo?',
      text: 'Esta acción aprobará el vuelo seleccionado.',
      icon: 'question',
      showCancelButton: true,
      confirmButtonText: 'Sí, aprobar',
      cancelButtonText: 'No, revisar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.vuelosService.apobrarSolicitudVuelo(id).subscribe({
          next: () => {
            Swal.fire('¡Aprobado!', 'El vuelo ha sido aprobado.', 'success');
            this.obtnerVuelos(); // Refresca la lista de vuelos después de aprobar
            this.cdr.detectChanges(); // Asegura que la vista se actualice con los nuevos datos
          },
          error: (error) => {
            console.error('Error al aprobar vuelo:', error);
            Swal.fire('Error', 'No se pudo aprobar el vuelo. Intente nuevamente.', 'error');
          }
        });
      }
    });
  }


}
