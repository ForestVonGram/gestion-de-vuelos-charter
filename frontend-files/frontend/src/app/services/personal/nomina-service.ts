import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthService } from '../auth/auth.service';
import { HttpParams } from '@angular/common/http';
import { HttpHeaders } from '@angular/common/http';
import { NominaDTO } from '../../models/personal/nomina-dto';




@Injectable({
  providedIn: 'root',
})
export class NominaService {
  
  constructor(private http: HttpClient, private authService: AuthService) {}

  apiUrl = `${environment.apiUrl}/nominas`;

  calcularNomina(nomina: NominaDTO): Observable<any> {
    const token = this.authService.getToken();
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
    return this.http.post(`${this.apiUrl}`, nomina, { headers });
  }

obtenerNominas(page: number = 0, estadoNomina?: string, mes?: number, anio?: number, personaId?: number): Observable<any> {
  const token = this.authService.getToken();
  console.log('Obteniendo nóminas con token:', token);
  const headers = new HttpHeaders({
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  });

  let params = new HttpParams().set('page', page.toString());

  // Solo agrega el param si tiene valor
  if (mes !== undefined && mes !== null && !isNaN(mes)) {
    params = params.set('mes', mes.toString());
  }
  if (anio !== undefined && anio !== null) {
    params = params.set('anio', anio.toString());
  }
  if (personaId !== undefined && personaId !== null) {
    params = params.set('personaId', personaId.toString());
  }
  if (estadoNomina) {
    params = params.set('estadoNomina', estadoNomina);
  }

  console.log('URL params:', params.toString());
  return this.http.get(`${this.apiUrl}`, { headers, params });
}
}
