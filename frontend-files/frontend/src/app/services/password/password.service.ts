import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

export interface ForgotPasswordRequest {
  email: string;
}

export interface VerificarCodigoRequest {
  email: string;
  codigo: string;
}

export interface ResetPasswordRequest {
  token: string;
  nuevaPassword: string;
}

export interface VerificarCodigoResponse {
  token: string;
}

@Injectable({
  providedIn: 'root'
})
export class PasswordService {
  private apiUrl = `${environment.apiUrl}/password`;

  constructor(private http: HttpClient) {}

  solicitarRecuperacion(email: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/forgot`, { email }).pipe(
      catchError(this.handleError)
    );
  }

  verificarCodigo(email: string, codigo: string): Observable<VerificarCodigoResponse> {
    return this.http.post<VerificarCodigoResponse>(`${this.apiUrl}/verify-code`, { email, codigo }).pipe(
      catchError(this.handleError)
    );
  }

  resetearPassword(token: string, nuevaPassword: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/reset`, { token, nuevaPassword }).pipe(
      catchError(this.handleError)
    );
  }

  validarToken(token: string): Observable<{ valido: boolean }> {
    return this.http.get<{ valido: boolean }>(`${this.apiUrl}/validate-token`, { params: { token } }).pipe(
      catchError(this.handleError)
    );
  }

  cambiarPassword(passwordActual: string, nuevaPassword: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/change`, { passwordActual, nuevaPassword }).pipe(
      catchError(this.handleError)
    );
  }

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'Error desconocido';

    if (error.error instanceof ErrorEvent) {
      errorMessage = `Error: ${error.error.message}`;
    } else {
      if (error.status === 400) {
        errorMessage = error.error?.message || 'Solicitud inválida';
      } else if (error.status === 0) {
        errorMessage = 'Error de conexión con el servidor';
      } else {
        errorMessage = error.error?.message || `Error ${error.status}: ${error.message}`;
      }
    }

    console.error('PasswordService error:', errorMessage);
    return throwError(() => error);
  }
}
