// services/flight.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class VuelosService {
  private apiUrl = `${environment.apiUrl}/flights`;

  constructor(private http: HttpClient) {}

  getProximosVuelos(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/proximos/${userId}`);
  }

  getHistorialVuelos(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/historial/${userId}`);
  }
}
