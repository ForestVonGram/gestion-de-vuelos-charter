import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface PagoCreateDTO {
  vueloId: number;
  monto: number;
  emailCliente: string;
  usuarioId: number;
  descripcion?: string;
}

export interface PagoDTO {
  id: number;
  vueloId: number;
  usuarioId: number;
  usuarioNombre: string;
  monto: number;
  estado: string;
  referenciaMercadoPago: string;
  numeroPreferencia: string;
  fechaPago: string;
  fechaCreacion: string;
  metodoPago: string;
  emailCliente: string;
  observaciones: string;
  urlPago: string;
}

@Injectable({
  providedIn: 'root'
})
export class PagoService {
  private apiUrl = `${environment.apiUrl}/pagos`;

  constructor(private http: HttpClient) {}

  iniciarPago(pago: PagoCreateDTO): Observable<PagoDTO> {
    return this.http.post<PagoDTO>(this.apiUrl, pago);
  }

  obtenerPagoPorId(id: number): Observable<PagoDTO> {
    return this.http.get<PagoDTO>(`${this.apiUrl}/${id}`);
  }

  confirmarPago(id: number, referencia: string): Observable<PagoDTO> {
    return this.http.post<PagoDTO>(`${this.apiUrl}/${id}/confirmar?referenciaMercadoPago=${referencia}`, {});
  }

  rechazarPago(id: number, motivo: string): Observable<PagoDTO> {
    return this.http.post<PagoDTO>(`${this.apiUrl}/${id}/rechazar?motivo=${motivo}`, {});
  }

  obtenerPagosPorVuelo(vueloId: number): Observable<PagoDTO[]> {
    return this.http.get<PagoDTO[]>(`${this.apiUrl}/vuelo/${vueloId}`);
  }

  obtenerPagosPorUsuario(usuarioId: number): Observable<PagoDTO[]> {
    return this.http.get<PagoDTO[]>(`${this.apiUrl}/usuario/${usuarioId}`);
  }
}
