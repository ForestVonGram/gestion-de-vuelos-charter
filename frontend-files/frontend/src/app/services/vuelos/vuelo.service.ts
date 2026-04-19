// src/app/services/vuelos/vuelo.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { TokenService } from '../token_service/token-service';

export interface VueloCreateDTO {
  usuarioId: number;
  origen: string;
  destino: string;
  fechaSalidaProgramada: string;
  fechaLlegadaProgramada: string;
  numeroPasajeros: number;
  proposito?: string;
  observaciones?: string;
}

export interface VueloDTO {
  id: number;
  usuarioId: number;
  usuarioNombre: string;
  aeronaveId: number | null;
  aeronaveMatricula: string | null;
  tripulacionIds: number[];
  origen: string;
  destino: string;
  fechaSalidaProgramada: string;
  fechaLlegadaProgramada: string;
  fechaSalidaReal: string | null;
  fechaLlegadaReal: string | null;
  numeroPasajeros: number;
  estado: string;
  proposito: string | null;
  observaciones: string | null;
  fechaSolicitud: string;
  costoEstimado: number | null;
}

export interface AsignacionAeronaveDTO {
  aeronaveId: number;
  observaciones?: string;
}

@Injectable({ providedIn: 'root' })
export class VueloService {
  private apiUrl = `${environment.apiUrl}/vuelos`;

  constructor(private http: HttpClient, private tokenService: TokenService) {}

  private getHeaders(): HttpHeaders {
    const token = this.tokenService.getToken();
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  crearVuelo(dto: VueloCreateDTO): Observable<VueloDTO> {
    return this.http.post<VueloDTO>(this.apiUrl, dto, { headers: this.getHeaders() });
  }

  asignarAeronave(vueloId: number, dto: AsignacionAeronaveDTO): Observable<VueloDTO> {
    return this.http.put<VueloDTO>(`${this.apiUrl}/${vueloId}/aeronave`, dto, { headers: this.getHeaders() });
  }

  /**
   * Obtiene todos los vuelos de un usuario específico.
   * Endpoint: GET /api/vuelos/usuario/{usuarioId}
   */
  getVuelosPorUsuario(usuarioId: number): Observable<VueloDTO[]> {
    console.log(`[VueloService] Solicitando vuelos para usuario ${usuarioId}`);
    return this.http.get<VueloDTO[]>(`${this.apiUrl}/usuario/${usuarioId}`, {
      headers: this.getHeaders()
    });
  }

  /**
   * Obtiene un vuelo por su ID.
   * Endpoint: GET /api/vuelos/{id}
   */
  getVueloById(id: number): Observable<VueloDTO> {
    return this.http.get<VueloDTO>(`${this.apiUrl}/${id}`, {
      headers: this.getHeaders()
    });
  }

  /**
   * Cancela un vuelo existente.
   * Endpoint: DELETE /api/vuelos/{id}
   */
  cancelarVuelo(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, {
      headers: this.getHeaders()
    });
  }
}
