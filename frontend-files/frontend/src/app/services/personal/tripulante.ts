import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient, HttpParams } from '@angular/common/http';
import { TokenService } from '../token_service/token-service';
import { HttpHeaders } from '@angular/common/http'; // Importamos HttpHeaders para agregar el token en las solicitudes HTTP
import { Observable } from 'rxjs';
import { CreateTripulanteDto } from '../../models/personal/create-tripulante-dto';

@Injectable({
  providedIn: 'root',
})
export class Tripulante {

  apiUrl = `${environment.apiUrl}/tripulantes`;
  constructor(private http: HttpClient, private tokenService: TokenService) {
  }

  registrarTripulante(data: CreateTripulanteDto): Observable<any> {

    const token= this.tokenService.getToken();
    const headers = new HttpHeaders({
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
    });
    return this.http.post(`${this.apiUrl}/registrar`, data, { headers });
  }

  eliminarTripulante(id: number): Observable<any> {
    const token= this.tokenService.getToken();
    const headers = new HttpHeaders
    ({
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
    });
    return this.http.delete(`${this.apiUrl}/${id}`, { headers });
  }
  editarTripulante(id: number, data: CreateTripulanteDto): Observable<any> {
    const token= this.tokenService.getToken();
    const headers = new HttpHeaders({
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
    });
    return this.http.put(`${this.apiUrl}/${id}`, data, { headers });
  }
  
  obtnerTripulantes(page: number): Observable<any> {
    const token= this.tokenService.getToken();
    const headers = new HttpHeaders({
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
    });
    let params = new HttpParams().set('page', page.toString());
    return this.http.get(`${this.apiUrl}/auxiliares`, { headers, params });
  }

  
}
