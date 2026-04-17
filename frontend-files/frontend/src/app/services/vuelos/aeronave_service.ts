import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { TokenService } from '../token_service/token-service';
import { HttpHeaders } from '@angular/common/http';
import { createAvion } from '../../models/avion/create-avion';
import { HttpParams } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class Aeronave {
  private apiUrl = `${environment.apiUrl}/aeronaves`;

  constructor(private http: HttpClient, private tokenService: TokenService) {}
  
  getAeronaves(): Observable<any[]> {

    const token = this.tokenService.getToken();
    const headers = {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    };
    return this.http.get<any[]>(this.apiUrl, { headers });
  }

  createAeronave(aeronaveData: createAvion): Observable<any> {
    const token = this.tokenService.getToken();
    const headers = {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    };
    return this.http.post(this.apiUrl, aeronaveData, { headers });
  }

  getAeronaveById(id: number): Observable<any> {
    const token = this.tokenService.getToken();
    const headers = {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    };
    return this.http.get(`${this.apiUrl}/${id}`, { headers });
  }

  actualizarAeronave(id: number, aeronaveData: any): Observable<any> {
    const token = this.tokenService.getToken();
    const headers = {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    };
    return this.http.put(`${this.apiUrl}/${id}`, aeronaveData, { headers });
  }

  eliminarAeronave(id: number): Observable<any> {
    const token = this.tokenService.getToken();
    const headers = {
      'Authorization': `Bearer ${token}`, 
      'Content-Type': 'application/json'
    };
    return this.http.delete(`${this.apiUrl}/${id}`, { headers });
  }

  subirImagenes(id: number, formData: FormData): Observable<any> {
  const token = this.tokenService.getToken();
  const headers = new HttpHeaders({
    'Authorization': `Bearer ${token}`
  });
  return this.http.post(`${this.apiUrl}/${id}/imagenes-multiples`, formData, { headers });
  }

  eliminarImagen(imagenId: number, aeronaveId: number): Observable<any> {
    const token = this.tokenService.getToken();
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    return this.http.delete(`${this.apiUrl}/${aeronaveId}/imagenes/${imagenId}`, { headers });
  }

  incrementarHorasVuelo(id: number, horas: number): Observable<any> {
    const token = this.tokenService.getToken();
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
    let params = new HttpParams().set('horasVuelo', horas.toString());
    return this.http.post(`${this.apiUrl}/${id}/incrementar-horas`, null, { headers, params });
  }
}
