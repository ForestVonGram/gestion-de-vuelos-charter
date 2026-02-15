import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';

export interface Mantenimiento {
  id: number;
  aeronaveId: number;
  aeronaveMatricula: string;
  tipo: string;
  descripcion: string;
  fechaInicio: string;
  fechaFin: string;
  responsableId: number;
  responsableNombre: string;
  costo: number;
  kilometrajeAeronave: number;
  horasVueloAeronave: number;
  observaciones: string;
  completado: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class MantenimientoService {

  private apiUrl = 'http://localhost:8080/api/mantenimientos';

  // Datos de prueba mientras el backend no esté listo
  private datosPrueba: Mantenimiento[] = [
    {
      id: 1,
      aeronaveId: 101,
      aeronaveMatricula: 'LV-ABC',
      tipo: 'PREVENTIVO',
      descripcion: 'Inspección anual de motor y sistemas',
      fechaInicio: '2024-01-15T08:00:00',
      fechaFin: '2024-01-20T17:00:00',
      responsableId: 5,
      responsableNombre: 'Carlos Rodríguez',
      costo: 2500,
      kilometrajeAeronave: 15000,
      horasVueloAeronave: 1200,
      observaciones: 'Cambio de aceite y filtros realizados. Todo en orden.',
      completado: true
    },
    {
      id: 2,
      aeronaveId: 102,
      aeronaveMatricula: 'LV-XYZ',
      tipo: 'CORRECTIVO',
      descripcion: 'Reparación de tren de aterrizaje',
      fechaInicio: '2024-02-01T09:30:00',
      fechaFin: '2024-02-05T14:20:00',
      responsableId: 3,
      responsableNombre: 'María González',
      costo: 4750,
      kilometrajeAeronave: 8200,
      horasVueloAeronave: 650,
      observaciones: 'Reemplazo de amortiguadores y revisión de sistema hidráulico',
      completado: true
    },
    {
      id: 3,
      aeronaveId: 103,
      aeronaveMatricula: 'LV-DEF',
      tipo: 'PREVENTIVO',
      descripcion: 'Mantenimiento de aviónica',
      fechaInicio: '2024-02-10T10:00:00',
      fechaFin: '',
      responsableId: 7,
      responsableNombre: 'Juan Pérez',
      costo: 1800,
      kilometrajeAeronave: 9500,
      horasVueloAeronave: 780,
      observaciones: 'Actualización de software de navegación pendiente',
      completado: false
    }
  ];

  constructor(private http: HttpClient) {}

  obtenerTodos(): Observable<Mantenimiento[]> {
    // return this.http.get<Mantenimiento[]>(this.apiUrl);
    // Por ahora retornamos datos de prueba
    return of(this.datosPrueba);
  }

  obtenerPorId(id: number): Observable<Mantenimiento> {
    // return this.http.get<Mantenimiento>(`${this.apiUrl}/${id}`);
    // Datos de prueba
    const mantenimiento = this.datosPrueba.find(m => m.id === id);

    if (mantenimiento) {
      return of(mantenimiento);
    } else {
      // Si no encuentra el mantenimiento, retorna un error
      return throwError(() => new Error(`Mantenimiento con id ${id} no encontrado`));
    }
  }

  obtenerPendientes(): Observable<Mantenimiento[]> {
    return this.http.get<Mantenimiento[]>(`${this.apiUrl}/pendientes`);
  }

  completarMantenimiento(id: number, fechaFin: string, observaciones?: string) {
    return this.http.patch(
      `${this.apiUrl}/${id}/completar`,
      null,
      {
        params: {
          fechaFin,
          observaciones: observaciones || ''
        }
      }
    );
  }
}
