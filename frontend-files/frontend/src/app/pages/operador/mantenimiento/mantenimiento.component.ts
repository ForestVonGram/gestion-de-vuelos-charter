import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MantenimientoService, Mantenimiento } from '../../../services/operador/mantenimiento/mantenimiento.service';
import { TruncatePipe } from '../pipes/truncate.pipe';

@Component({
  selector: 'app-mantenimiento',
  templateUrl: './mantenimiento.component.html',
  styleUrls: ['./mantenimiento.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, TruncatePipe]
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

  constructor(
    private mantenimientoService: MantenimientoService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.cargarMantenimientos();
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
      // Filtro por estado
      if (this.filtroEstado === 'pendientes' && m.completado) return false;
      if (this.filtroEstado === 'completados' && !m.completado) return false;

      // Filtro por búsqueda
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
    this.router.navigate(['/mantenimientos/nuevo']);
  }
}
