import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

export interface Mantenimiento {
  id: number;
  aeronaveId: number;
  aeronaveMatricula: string;
  tipo: string;
  descripcion: string;
  fechaInicio: string;
  fechaFin?: string;
  responsableId: number;
  responsableNombre: string;
  costo: number;
  kilometrajeAeronave: number;
  horasVueloAeronave: number;
  observaciones?: string;
  completado: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class MantenimientoService {
  private apiUrl = `${environment.apiUrl}/api/mantenimientos`;

  constructor(private http: HttpClient) {}

  // GET /api/mantenimientos
  obtenerTodos(): Observable<Mantenimiento[]> {
    return this.http.get<Mantenimiento[]>(this.apiUrl);
  }

  // GET /api/mantenimientos/{id}
  obtenerPorId(id: number): Observable<Mantenimiento> {
    return this.http.get<Mantenimiento>(`${this.apiUrl}/${id}`);
  }

  // GET /api/mantenimientos/pendientes
  obtenerPendientes(): Observable<Mantenimiento[]> {
    return this.http.get<Mantenimiento[]>(`${this.apiUrl}/pendientes`);
  }

  // GET /api/mantenimientos/tipo/{tipo}
  obtenerPorTipo(tipo: string): Observable<Mantenimiento[]> {
    return this.http.get<Mantenimiento[]>(`${this.apiUrl}/tipo/${tipo}`);
  }

  // GET /api/mantenimientos/aeronave/{aeronaveId}
  obtenerPorAeronave(aeronaveId: number): Observable<Mantenimiento[]> {
    return this.http.get<Mantenimiento[]>(`${this.apiUrl}/aeronave/${aeronaveId}`);
  }

  // GET /api/mantenimientos/aeronave/{aeronaveId}/pendientes
  obtenerPendientesPorAeronave(aeronaveId: number): Observable<Mantenimiento[]> {
    return this.http.get<Mantenimiento[]>(`${this.apiUrl}/aeronave/${aeronaveId}/pendientes`);
  }

  // GET /api/mantenimientos/aeronave/{aeronaveId}/tipo/{tipo}
  obtenerPorAeronaveYTipo(aeronaveId: number, tipo: string): Observable<Mantenimiento[]> {
    return this.http.get<Mantenimiento[]>(`${this.apiUrl}/aeronave/${aeronaveId}/tipo/${tipo}`);
  }

  // GET /api/mantenimientos/fecha?inicio={inicio}&fin={fin}
  obtenerPorRangoFechas(inicio: Date, fin: Date): Observable<Mantenimiento[]> {
    return this.http.get<Mantenimiento[]>(`${this.apiUrl}/fecha`, {
      params: {
        inicio: inicio.toISOString(),
        fin: fin.toISOString()
      }
    });
  }

  // GET /api/mantenimientos/aeronave/{aeronaveId}/ultimos
  obtenerUltimosPorAeronave(aeronaveId: number): Observable<Mantenimiento[]> {
    return this.http.get<Mantenimiento[]>(`${this.apiUrl}/aeronave/${aeronaveId}/ultimos`);
  }

  // GET /api/mantenimientos/responsable/{responsableId}
  obtenerPorResponsable(responsableId: number): Observable<Mantenimiento[]> {
    return this.http.get<Mantenimiento[]>(`${this.apiUrl}/responsable/${responsableId}`);
  }

  // POST /api/mantenimientos
  crearMantenimiento(mantenimiento: any): Observable<Mantenimiento> {
    return this.http.post<Mantenimiento>(this.apiUrl, mantenimiento);
  }

  // PATCH /api/mantenimientos/{id}/completar
  completarMantenimiento(id: number, fechaFin: string, observaciones?: string): Observable<Mantenimiento> {
    return this.http.patch<Mantenimiento>(`${this.apiUrl}/${id}/completar`, null, {
      params: {
        fechaFin: fechaFin,
        ...(observaciones && { observaciones })
      }
    });
  }
}
