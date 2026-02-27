import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface ForgotPasswordRequest {
  email: string;
}

export interface ResetPasswordRequest {
  token: string;
  nuevaPassword: string;
}

export interface VerificacionCodigoRequest {
  codigo: string;
}

@Injectable({
  providedIn: 'root'
})
export class PasswordService {
  private apiUrl = `${environment.apiUrl}/password`;

  constructor(private http: HttpClient) {}

  solicitarRecuperacion(email: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/forgot`, { email });
  }

  validarToken(token: string): Observable<{ valido: boolean }> {
    return this.http.get<{ valido: boolean }>(`${this.apiUrl}/validate-token?token=${token}`);
  }

  resetearPassword(token: string, nuevaPassword: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/reset`, { token, nuevaPassword });
  }

  verificarCodigo(email: string, codigo: string): Observable<{ token: string }> {
    console.log('Verificando código:', codigo, 'para email:', email); // Para debug
    return this.http.post<{ token: string }>(`${this.apiUrl}/verify-code`, {
      email: email,
      codigo: codigo
    });
  }
}
