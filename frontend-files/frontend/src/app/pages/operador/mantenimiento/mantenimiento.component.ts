import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MantenimientoService, Mantenimiento } from '../../../services/operador/mantenimiento/mantenimiento.service';

@Component({
  selector: 'app-mantenimiento',
  templateUrl: './mantenimiento.component.html',
  styleUrls: ['./mantenimiento.component.css'],
  standalone: true,
  imports: [CommonModule]
})
export class MantenimientoComponent implements OnInit {

  mantenimientos: Mantenimiento[] = [];

  pendientes = 0;
  completados = 0;
  costoTotal = 0;
  enProceso = 0;

  constructor(private mantenimientoService: MantenimientoService) {}

  ngOnInit(): void {
    this.cargarMantenimientos();
  }

  cargarMantenimientos() {
    this.mantenimientoService.obtenerTodos().subscribe(data => {
      this.mantenimientos = data;
      this.calcularMetricas();
    });
  }

  calcularMetricas() {
    this.pendientes = this.mantenimientos.filter(m => !m.completado).length;
    this.completados = this.mantenimientos.filter(m => m.completado).length;

    this.costoTotal = this.mantenimientos.reduce(
      (total, m) => total + (m.costo || 0),
      0
    );
  }
}
