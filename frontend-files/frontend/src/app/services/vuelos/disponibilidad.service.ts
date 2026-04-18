import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { TokenService } from '../token_service/token-service';

export interface AeronaveDTO {
  id: number;
  matricula: string;
  modelo: string;
  capacidadPasajeros: number;
  // ...otros campos
}

@Injectable({ providedIn: 'root' })
export class DisponibilidadService {
  private apiUrl = `${environment.apiUrl}/disponibilidad`;

  constructor(private http: HttpClient, private tokenService: TokenService) {}

  private getHeaders(): HttpHeaders {
    const token = this.tokenService.getToken();
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  consultarAeronavesDisponibles(
    fechaInicio: string,
    fechaFin: string,
    capacidadMinima?: number
  ): Observable<AeronaveDTO[]> {
    let params = new HttpParams()
      .set('fechaInicio', fechaInicio)
      .set('fechaFin', fechaFin);
    if (capacidadMinima) {
      params = params.set('capacidadMinima', capacidadMinima.toString());
    }
    return this.http.get<AeronaveDTO[]>(`${this.apiUrl}/aeronaves-disponibles`, {
      headers: this.getHeaders(),
      params
    });
  }
}
