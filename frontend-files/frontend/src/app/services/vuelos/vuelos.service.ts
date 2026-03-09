// services/flight.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class VuelosService {
  // Endpoint base configurado para el recurso de vuelos (flights)
  private apiUrl = `${environment.apiUrl}/flights`;

  constructor(private http: HttpClient) {}

  /**
   * Obtiene la lista de vuelos programados para un usuario específico.
   * Útil para mostrar en el Dashboard principal las próximas misiones.
   * @param userId Identificador único del tripulante o piloto.
   * @returns Observable con un array de vuelos futuros.
   */
  getProximosVuelos(userId: number): Observable<any[]> {
    // GET /api/flights/proximos/{userId}
    return this.http.get<any[]>(`${this.apiUrl}/proximos/${userId}`);
  }

  /**
   * Recupera el registro histórico de todos los vuelos realizados por el usuario.
   * Es fundamental para el cálculo de horas de vuelo totales en el perfil del tripulante.
   * @param userId Identificador único del tripulante.
   * @returns Observable con el historial completo de misiones finalizadas.
   */
  getHistorialVuelos(userId: number): Observable<any[]> {
    // GET /api/flights/historial/{userId}
    return this.http.get<any[]>(`${this.apiUrl}/historial/${userId}`);
  }
}
