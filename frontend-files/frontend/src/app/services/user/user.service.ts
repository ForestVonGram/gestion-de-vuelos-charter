import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment} from '../../../environments/environment';
import { TokenService } from '../token_service/token-service';

export interface Usuario {
  id: number;
  nombre: string;
  apellido: string;
  email: string;
  telefono: string;
  rol: string;
  fechaRegistro: string;
  activo: boolean;
  dosFactoresHabilitado: boolean;
  metodoDosFactores?: 'EMAIL' | 'SMS';
}

export interface UsuarioUpdate {
  nombre?: string;
  apellido?: string;
  email?: string;
  telefono?: string;
  dosFactoresHabilitado?: boolean;
  metodoDosFactores?: 'EMAIL' | 'SMS' | null;
}

export interface CambiarPasswordRequest {
  nuevaPassword: string;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = environment.apiUrl + '/usuarios';

  constructor(private http: HttpClient, private tokenService: TokenService) {}

  getUser(id: number): Observable<Usuario> {
    return this.http.get<Usuario>(`${this.apiUrl}/${id}`);
  }

  updateUser(id: number, data: UsuarioUpdate): Observable<Usuario> {
    return this.http.put<Usuario>(`${this.apiUrl}/${id}`, data);
  }

  changePassword(id: number, nuevaPassword: string): Observable<void> {
    const body: CambiarPasswordRequest = { nuevaPassword };
    return this.http.post<void>(`${this.apiUrl}/${id}/cambiar-password`, body);
  }

  deactivateUser(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getAllUsers(): Observable<Usuario[]> {
    const token = this.tokenService.getToken();
    const headers = {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    };
    return this.http.get<Usuario[]>(`${this.apiUrl}`, { headers });

  }

}
