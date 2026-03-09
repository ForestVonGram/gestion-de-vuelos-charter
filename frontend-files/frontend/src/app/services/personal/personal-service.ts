import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { TokenService } from '../token_service/token-service';
import { PersonalCreateDTO } from '../../models/personal/create-personal-dto';

@Injectable({
  providedIn: 'root',
})
export class PersonalService {

  // Endpoint base definido en el archivo de configuración del entorno
  apiUrl = `${environment.apiUrl}/personal`;

  constructor(private http: HttpClient, private tokenService: TokenService) {}

  /**
   * Registra un nuevo miembro del personal (pilotos, técnicos, administrativos).
   * @param data Objeto de tipo PersonalCreateDTO con la información necesaria.
   */
  crearPersonal(data: PersonalCreateDTO): Observable<any> {
    const token = this.tokenService.getToken();

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    // Envío de datos mediante POST al endpoint de registro
    return this.http.post(`${this.apiUrl}/registrar`, data, { headers });
  }

  /**
   * Obtiene la lista completa del personal registrado.
   */
  obtenerPersonal(): Observable<any> {
    const token = this.tokenService.getToken();
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    return this.http.get(`${this.apiUrl}`, { headers });
  }

  /**
   * Realiza una búsqueda avanzada de personal aplicando filtros opcionales.
   * @param nombre Filtro por nombre o apellido.
   * @param estado Filtro por estado operativo (Activo, Inactivo, En Vuelo).
   * @param cargo Filtro por posición jerárquica o técnica.
   */
  filtroPersonal(nombre: string, estado: string, cargo: string): Observable<any> {
    const token = this.tokenService.getToken();
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    // Uso de HttpParams para construir la Query String de forma dinámica y segura
    let params = new HttpParams();
    if (nombre) params = params.set('nombre', nombre);
    if (estado) params = params.set('estado', estado);
    if (cargo)  params = params.set('cargo', cargo);

    // Logs de depuración para verificar la construcción de la URL en desarrollo
    console.log('URL final:', `${this.apiUrl}/filtros`);
    console.log('Params:', params.toString());

    return this.http.get(`${this.apiUrl}/filtros`, { headers, params });
  }
}
