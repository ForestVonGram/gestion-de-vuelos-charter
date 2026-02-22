import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { MantenimientoService, Mantenimiento } from '../../../services/operador/mantenimiento/mantenimiento.service';
import { TruncatePipe } from '../pipes/truncate.pipe';
import { AuthService } from '../../../services/auth/auth.service';

@Component({
  selector: 'app-mantenimiento',
  templateUrl: './mantenimiento.component.html',
  styleUrls: ['./mantenimiento.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, TruncatePipe, RouterModule]
})
export class MantenimientoComponent implements OnInit {
  mantenimientos: Mantenimiento[] = [];
  mantenimientosFiltrados: Mantenimiento[] = [];

  pendientes = 0;
  completados = 0;
  enProceso = 0;
  costoTotal = 0;

  filtroBusqueda: string = '';
  filtroEstado: string = 'todos';

  // Propiedades para el dropdown de usuario
  userName: string = 'Operador';
  userEmail: string = '';
  isDropdownOpen: boolean = false;

  constructor(
    private mantenimientoService: MantenimientoService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.cargarMantenimientos();
    this.cargarDatosUsuario();
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

  cargarMantenimientos() {
    this.mantenimientoService.obtenerTodos().subscribe(data => {
      this.mantenimientos = data;
      this.mantenimientosFiltrados = [...this.mantenimientos];
      this.calcularMetricas();
      this.aplicarFiltros();
    });
  }

  calcularMetricas() {
    this.pendientes = this.mantenimientos.filter(m => !m.completado).length;
    this.completados = this.mantenimientos.filter(m => m.completado).length;
    this.enProceso = this.mantenimientos.filter(m => !m.completado && m.fechaInicio).length;

    this.costoTotal = this.mantenimientos.reduce(
      (total, m) => total + (m.costo || 0),
      0
    );
  }

  filtrarMantenimientos() {
    this.aplicarFiltros();
  }

  aplicarFiltros() {
    this.mantenimientosFiltrados = this.mantenimientos.filter(m => {
      if (this.filtroEstado === 'pendientes' && m.completado) return false;
      if (this.filtroEstado === 'completados' && !m.completado) return false;

      if (this.filtroBusqueda) {
        const busqueda = this.filtroBusqueda.toLowerCase();
        return m.aeronaveMatricula.toLowerCase().includes(busqueda) ||
          m.responsableNombre.toLowerCase().includes(busqueda) ||
          m.descripcion.toLowerCase().includes(busqueda);
      }

      return true;
    });
  }

  verDetalle(id: number) {
    this.router.navigate(['/operador/mantenimiento', id]);
  }

  nuevoMantenimiento() {
    this.router.navigate(['/operador/mantenimiento/nuevo']);
  }
}
