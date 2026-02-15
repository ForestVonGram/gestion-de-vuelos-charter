import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

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

  constructor(private http: HttpClient) {}

  obtenerTodos(): Observable<Mantenimiento[]> {
    return this.http.get<Mantenimiento[]>(this.apiUrl);
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
