import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

// Interfaz que define la estructura de un objeto de Mantenimiento según el Backend
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
  // La URL base se construye dinámicamente desde el archivo de configuración environment
  private apiUrl = `${environment.apiUrl}/api/mantenimientos`;

  constructor(private http: HttpClient) {}

  /** * OPERACIONES GENERALES
   */

  // Obtiene el historial completo de mantenimientos de la empresa
  obtenerTodos(): Observable<Mantenimiento[]> {
    return this.http.get<Mantenimiento[]>(this.apiUrl);
  }

  // Busca una orden de mantenimiento específica por su ID
  obtenerPorId(id: number): Observable<Mantenimiento> {
    return this.http.get<Mantenimiento>(`${this.apiUrl}/${id}`);
  }

  // Filtra solo los mantenimientos que aún no han sido marcados como completados
  obtenerPendientes(): Observable<Mantenimiento[]> {
    return this.http.get<Mantenimiento[]>(`${this.apiUrl}/pendientes`);
  }

  /** * FILTROS ESPECIALIZADOS
   */

  // Filtra por tipo (PREVENTIVO, CORRECTIVO, REPOSTAJE, INSPECCION)
  obtenerPorTipo(tipo: string): Observable<Mantenimiento[]> {
    return this.http.get<Mantenimiento[]>(`${this.apiUrl}/tipo/${tipo}`);
  }

  // Obtiene todos los registros asociados a una aeronave en particular
  obtenerPorAeronave(aeronaveId: number): Observable<Mantenimiento[]> {
    return this.http.get<Mantenimiento[]>(`${this.apiUrl}/aeronave/${aeronaveId}`);
  }

  // Filtra por un rango de tiempo específico (útil para reportes mensuales/anuales)
  obtenerPorRangoFechas(inicio: Date, fin: Date): Observable<Mantenimiento[]> {
    return this.http.get<Mantenimiento[]>(`${this.apiUrl}/fecha`, {
      params: {
        inicio: inicio.toISOString(),
        fin: fin.toISOString()
      }
    });
  }

  // Obtiene las órdenes asignadas a un técnico o responsable específico
  obtenerPorResponsable(responsableId: number): Observable<Mantenimiento[]> {
    return this.http.get<Mantenimiento[]>(`${this.apiUrl}/responsable/${responsableId}`);
  }

  /** * CREACIÓN Y ACTUALIZACIÓN
   */

  // Envía los datos de una nueva orden de mantenimiento al servidor
  crearMantenimiento(mantenimiento: any): Observable<Mantenimiento> {
    return this.http.post<Mantenimiento>(this.apiUrl, mantenimiento);
  }

  // Finaliza un mantenimiento abierto actualizando la fecha de fin y observaciones
  completarMantenimiento(id: number, fechaFin: string, observaciones?: string): Observable<Mantenimiento> {
    // Se usa PATCH ya que solo estamos actualizando campos específicos de un recurso existente
    return this.http.patch<Mantenimiento>(`${this.apiUrl}/${id}/completar`, null, {
      params: {
        fechaFin: fechaFin,
        ...(observaciones && { observaciones })
      }
    });
  }
}
