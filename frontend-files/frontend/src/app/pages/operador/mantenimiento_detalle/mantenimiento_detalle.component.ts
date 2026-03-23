import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { MantenimientoService, Mantenimiento } from '../../../services/operador/mantenimiento/mantenimiento.service';
import {AccesibilidadComponent} from '../../../shared/accesibilidad/accesibilidad.component';

@Component({
  selector: 'app-mantenimiento-detalle',
  templateUrl: './mantenimiento_detalle.component.html',
  styleUrls: ['./mantenimiento_detalle.component.css'],
  standalone: true,
  imports: [CommonModule, AccesibilidadComponent]
})
export class MantenimientoDetalleComponent implements OnInit {
  // Objeto que almacena la información detallada de la orden de mantenimiento
  mantenimiento: Mantenimiento | undefined;

  constructor(
    private route: ActivatedRoute, // Para acceder a los parámetros de la URL
    private router: Router,         // Para la navegación entre vistas
    private mantenimientoService: MantenimientoService // Servicio de datos
  ) {}

  ngOnInit(): void {
    // Paso 1: Extraer el ID del mantenimiento desde la ruta (ej: /mantenimiento/5)
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id) {
      this.cargarMantenimiento(id);
    }
  }

  // Paso 2: Consultar al servidor los datos específicos del mantenimiento por su ID
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

  // Método para regresar a la lista general de mantenimientos
  volver(): void {
    this.router.navigate(['/operador/mantenimiento']);
  }

  // Lógica para finalizar una orden de mantenimiento abierta
  completarMantenimiento(): void {
    // Solicita confirmación y genera la fecha de finalización actual en formato ISO
    if (this.mantenimiento && confirm('¿Está seguro de marcar este mantenimiento como completado?')) {
      const fechaFin = new Date().toISOString();

      // Llama al servicio para actualizar el estado en la base de datos
      this.mantenimientoService.completarMantenimiento(this.mantenimiento.id, fechaFin).subscribe({
        next: () => {
          alert('Mantenimiento marcado como completado');
          // Recarga los datos para refrescar la interfaz con el nuevo estado
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
