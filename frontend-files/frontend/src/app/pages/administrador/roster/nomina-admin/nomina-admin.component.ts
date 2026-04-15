import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AdminSidebarComponent } from '../../../../shared/admin-sidebar/admin-sidebar.component';
import { AuthService, User } from '../../../../services/auth/auth.service';
import { AccesibilidadComponent } from '../../../../shared/accesibilidad/accesibilidad.component';
import { NominaService } from '../../../../services/personal/nomina-service';
import { EstadoNomina } from '../../../../models/personal/estado-nomina';
import { NominaDTO } from '../../../../models/personal/nomina-dto';
import { ChangeDetectorRef } from '@angular/core';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-nomina-admin',
  standalone: true,
  imports: [CommonModule, RouterModule, AdminSidebarComponent],
  templateUrl: './nomina-admin.component.html',
  styleUrls: ['./nomina-admin.component.css']
})
export class NominaAdminComponent implements OnInit {

  currentUser: User | null = null;
  estados = Object.values(EstadoNomina);
  nominas: NominaDTO[] = [];
  meses = ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'];


  // Paginación
  paginaActual: number = 0;
  totalPaginas: number = 0;
  totalElementos: number = 0;

  // Filtros
  filtroEstado?: string;
  filtroMes?: number;
  filtroAnio?: number;
  filtroPersonaId?: number;

  constructor(
    private authService: AuthService,
    private router: Router,
    private nominaService: NominaService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.currentUser = this.authService.currentUserValue;
    this.obtenerNominas();
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }

  getNombreMes(mesNumero: number): string {
    return this.meses[mesNumero - 1] || 'N/A';
  }

  obtenerNominas(): void {
    this.nominaService.obtenerNominas(
      this.paginaActual,
      this.filtroEstado,
      this.filtroMes,
      this.filtroAnio,
      this.filtroPersonaId
    ).subscribe({
      next: (response) => {
        this.nominas = response.content;
        this.totalPaginas = response.totalPages;
        this.totalElementos = response.totalElements;
        this.cdr.detectChanges(); // Forzar detección de cambios
      },
      error: (error) => console.error('Error al obtener las nóminas:', error)
    });
  }

  eliminarNomina(id: number): void {
    Swal.fire({
      title: '¿Estás seguro?',
      text: 'Esta acción no se puede deshacer.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.nominaService.eliminarNomina(id).subscribe({
          next: () => {
            Swal.fire('¡Eliminada!', 'La nómina ha sido eliminada exitosamente.', 'success');
            this.obtenerNominas(); // Refrescar la lista después de eliminar
          },
          error: (error) => {
            console.error('Error al eliminar la nómina:', error);
            Swal.fire('Error', 'Ocurrió un error al eliminar la nómina. Por favor, inténtelo de nuevo.', 'error');
          }
        });
      }
    });
  }


  irAPagina(pagina: number): void {
    if (pagina >= 0 && pagina < this.totalPaginas) {
      this.paginaActual = pagina;
      this.obtenerNominas();
    }
  }



  paginaAnterior(): void { this.irAPagina(this.paginaActual - 1); }
  paginaSiguiente(): void { this.irAPagina(this.paginaActual + 1); }

  getEstadoClase(estado: string): string {
    switch (estado?.toLowerCase()) {
      case 'pagada': return 'badge-success';
      case 'pendiente': return 'badge-warning';
      case 'cancelada': return 'badge-error';
      default: return 'badge-neutral';
    }
  }
}