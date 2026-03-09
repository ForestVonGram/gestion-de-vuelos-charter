import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { TokenService } from '../token_service/token-service'; // Importamos TokenService para manejar el token de autenticación
import { HttpHeaders } from '@angular/common/http'; // Importamos HttpHeaders para agregar el token en las solicitudes HTTP
import { PersonalCreateDTO } from '../../models/personal/create-personal-dto';
import { HttpParams } from '@angular/common/http'; // Importamos HttpParams para manejar los parámetros de consulta en las solicitudes HTTP


@Injectable({
  providedIn: 'root',
})
export class PersonalService {

  apiUrl = `${environment.apiUrl}/personal`;

  constructor(private http: HttpClient, private tokenService: TokenService) {
  }

  crearPersonal(data: PersonalCreateDTO): Observable<any> {

    const token= this.tokenService.getToken();

    const headers = new HttpHeaders({
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
    });

    return this.http.post(`${this.apiUrl}/registrar`, data, { headers });
  }

  obtenerPersonal(): Observable<any> {

    const token= this.tokenService.getToken();
    const headers = new HttpHeaders({
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
    });
    return this.http.get(`${this.apiUrl}`, { headers });
  }

  filtroPersonal(nombre: string, estado: string, cargo: string): Observable<any> {
    const token = this.tokenService.getToken();
    const headers = new HttpHeaders({
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
    });

    let params = new HttpParams();
    if (nombre) params = params.set('nombre', nombre);
    if (estado) params = params.set('estado', estado);
    if (cargo)  params = params.set('cargo', cargo);


    return this.http.get(`${this.apiUrl}/filtros`, { headers, params });
  }

  eliminarPersonal(id: number): Observable<any>   {
    const token = this.tokenService.getToken();
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
    });
    return this.http.put(`${this.apiUrl}/${id}/desactiva`, {}, { headers, responseType: 'text' });
  }

  activarPersonal(id: number): Observable<any>   {
    const token = this.tokenService.getToken();
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
    });
    return this.http.put(`${this.apiUrl}/${id}/activa`, {}, { headers, responseType: 'text' });
  }

}
