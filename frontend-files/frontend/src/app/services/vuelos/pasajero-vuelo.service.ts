import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { TokenService } from '../token_service/token-service';

export interface PasajeroVueloCreateDTO {
  vueloId: number;
  nombre: string;
  apellido: string;
  documentoIdentidad: string;
  tipoDocumento?: string;
  nacionalidad?: string;
  telefono?: string;
  email?: string;
  contactoEmergencia?: string;
  telefonoEmergencia?: string;
  restriccionesMedicas?: string;
  restriccionesAlimentarias?: string;
  equipajeEspecial?: string;
  asientoPreferido?: string;
  observaciones?: string;
}

export interface PasajeroVueloDTO {
  id: number;
  vueloId: number;
  nombre: string;
  apellido: string;
  nombreCompleto: string;
  documentoIdentidad: string;
  tipoDocumento: string;
  nacionalidad: string;
  telefono: string;
  email: string;
  contactoEmergencia: string | null;
  telefonoEmergencia: string | null;
  restriccionesMedicas: string | null;
  restriccionesAlimentarias: string | null;
  equipajeEspecial: string | null;
  asientoPreferido: string | null;
  observaciones: string | null;
}

@Injectable({ providedIn: 'root' })
export class PasajeroVueloService {
  private apiUrl = `${environment.apiUrl}/pasajeros-vuelo`;

  constructor(private http: HttpClient, private tokenService: TokenService) {}

  private getHeaders(): HttpHeaders {
    const token = this.tokenService.getToken();
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  crearPasajero(dto: PasajeroVueloCreateDTO): Observable<PasajeroVueloDTO> {
    return this.http.post<PasajeroVueloDTO>(this.apiUrl, dto, { headers: this.getHeaders() });
  }
}
