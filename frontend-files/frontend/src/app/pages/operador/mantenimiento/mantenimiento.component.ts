import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { MantenimientoService, Mantenimiento } from '../../../services/operador/mantenimiento/mantenimiento.service';
import { TruncatePipe } from '../pipes/truncate.pipe';
import { AuthService } from '../../../services/auth/auth.service';
import {AccesibilidadComponent} from '../../../shared/accesibilidad/accesibilidad.component';

@Component({
  selector: 'app-mantenimiento',
  templateUrl: './mantenimiento.component.html',
  styleUrls: ['./mantenimiento.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, TruncatePipe, RouterModule, AccesibilidadComponent]
})
export class MantenimientoComponent implements OnInit {
  // --- Listados de datos ---
  mantenimientos: Mantenimiento[] = [];         // Lista maestra proveniente del servidor
  mantenimientosFiltrados: Mantenimiento[] = []; // Lista que se muestra en la UI tras aplicar filtros

  // --- Métricas operativas ---
  pendientes = 0;
  completados = 0;
  enProceso = 0;
  costoTotal = 0;

  // --- Estados de filtros ---
  filtroBusqueda: string = '';
  filtroEstado: string = 'todos';

  // --- Propiedades de perfil y menú ---
  userName: string = 'Operador';
  userEmail: string = '';
  isDropdownOpen: boolean = false;

  constructor(
    private mantenimientoService: MantenimientoService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.cargarMantenimientos(); // Carga inicial de datos de mantenimiento
    this.cargarDatosUsuario();   // Recupera info del usuario logueado
  }

  // Obtiene los datos del usuario desde el AuthService para personalizar la interfaz
  cargarDatosUsuario(): void {
    const currentUser = this.authService.currentUserValue;
    if (currentUser) {
      this.userName = currentUser.nombreCompleto || 'Operador';
      this.userEmail = currentUser.email || '';
    }
  }

  // Controla la visibilidad del menú desplegable del perfil
  toggleDropdown(): void {
    this.isDropdownOpen = !this.isDropdownOpen;
  }

  // Cierra la sesión y redirige al login
  logout(): void {
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }

  // Consume el servicio para obtener todos los registros de mantenimiento
  cargarMantenimientos() {
    this.mantenimientoService.obtenerTodos().subscribe(data => {
      this.mantenimientos = data;
      this.mantenimientosFiltrados = [...this.mantenimientos];
      this.calcularMetricas(); // Procesa totales y contadores
      this.aplicarFiltros();   // Ejecuta filtros si existen valores previos
    });
  }

  // Calcula indicadores clave (KPIs) basados en el estado y costo de los mantenimientos
  calcularMetricas() {
    this.pendientes = this.mantenimientos.filter(m => !m.completado).length;
    this.completados = this.mantenimientos.filter(m => m.completado).length;
    // Se asume "en proceso" si no está completado pero ya tiene fecha de inicio
    this.enProceso = this.mantenimientos.filter(m => !m.completado && m.fechaInicio).length;

    // Sumatoria total de costos de mantenimiento acumulados
    this.costoTotal = this.mantenimientos.reduce(
      (total, m) => total + (m.costo || 0),
      0
    );
  }

  // Método disparado desde el HTML al escribir en el buscador o cambiar el select
  filtrarMantenimientos() {
    this.aplicarFiltros();
  }

  // Lógica de filtrado combinada: Estado (Pendiente/Completado) + Texto libre
  aplicarFiltros() {
    this.mantenimientosFiltrados = this.mantenimientos.filter(m => {
      // Filtro por estado
      if (this.filtroEstado === 'pendientes' && m.completado) return false;
      if (this.filtroEstado === 'completados' && !m.completado) return false;

      // Filtro por búsqueda de texto (Matrícula, Responsable o Descripción)
      if (this.filtroBusqueda) {
        const busqueda = this.filtroBusqueda.toLowerCase();
        return m.aeronaveMatricula.toLowerCase().includes(busqueda) ||
          m.responsableNombre.toLowerCase().includes(busqueda) ||
          m.descripcion.toLowerCase().includes(busqueda);
      }

      return true;
    });
  }

  // Navegación al detalle individual de un mantenimiento
  verDetalle(id: number) {
    this.router.navigate(['/operador/mantenimiento', id]);
  }

  // Navegación al formulario de creación
  nuevoMantenimiento() {
    this.router.navigate(['/operador/mantenimiento/nuevo']);
  }
}
