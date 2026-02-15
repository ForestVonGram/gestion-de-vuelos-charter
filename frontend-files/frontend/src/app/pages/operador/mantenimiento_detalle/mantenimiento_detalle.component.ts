import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { MantenimientoService, Mantenimiento } from '../../../services/operador/mantenimiento/mantenimiento.service';

@Component({
  selector: 'app-mantenimiento-detalle',
  templateUrl: './mantenimiento_detalle.component.html',
  styleUrls: ['./mantenimiento_detalle.component.css'],
  standalone: true,
  imports: [CommonModule]
})
export class MantenimientoDetalleComponent implements OnInit {
  mantenimiento: Mantenimiento | undefined;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private mantenimientoService: MantenimientoService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id) {
      this.cargarMantenimiento(id);
    }
  }

  cargarMantenimiento(id: number): void {
    this.mantenimientoService.obtenerPorId(id).subscribe({
      next: (data) => {
        this.mantenimiento = data;
      },
      error: (error) => {
        console.error('Error al cargar el mantenimiento:', error);
      }
    });
  }

  volver(): void {
    this.router.navigate(['/operador/mantenimiento']);
  }

  completarMantenimiento(): void {
    if (this.mantenimiento && confirm('¿Está seguro de marcar este mantenimiento como completado?')) {
      const fechaFin = new Date().toISOString();
      this.mantenimientoService.completarMantenimiento(this.mantenimiento.id, fechaFin).subscribe({
        next: () => {
          alert('Mantenimiento marcado como completado');
          this.cargarMantenimiento(this.mantenimiento!.id);
        },
        error: (error) => {
          console.error('Error al completar mantenimiento:', error);
          alert('Error al completar el mantenimiento');
        }
      });
    }
  }
}
