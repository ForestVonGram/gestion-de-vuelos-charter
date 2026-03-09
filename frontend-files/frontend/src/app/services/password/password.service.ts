import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

// --- Interfaces de Datos (Contratos con el API) ---
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
  token: string; // Token temporal otorgado tras verificar el código enviado por email
}

@Injectable({
  providedIn: 'root'
})
export class PasswordService {
  private apiUrl = `${environment.apiUrl}/password`;

  constructor(private http: HttpClient) {}

  /**
   * Paso 1: Solicita el envío de un código de recuperación al correo del usuario.
   */
  solicitarRecuperacion(email: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/forgot`, { email }).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Paso 2: Valida el código que el usuario recibió en su correo.
   * Retorna un token temporal de un solo uso para proceder al reset.
   */
  verificarCodigo(email: string, codigo: string): Observable<VerificarCodigoResponse> {
    return this.http.post<VerificarCodigoResponse>(`${this.apiUrl}/verify-code`, { email, codigo }).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Paso 3: Realiza el cambio definitivo de contraseña usando el token de validación.
   */
  resetearPassword(token: string, nuevaPassword: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/reset`, { token, nuevaPassword }).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Verifica si un token de recuperación sigue siendo vigente (no ha expirado).
   */
  validarToken(token: string): Observable<{ valido: boolean }> {
    return this.http.get<{ valido: boolean }>(`${this.apiUrl}/validate-token`, { params: { token } }).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Cambio de contraseña estándar para usuarios con sesión ya iniciada.
   */
  cambiarPassword(passwordActual: string, nuevaPassword: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/change`, { passwordActual, nuevaPassword }).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Manejador centralizado de errores para todas las peticiones de este servicio.
   */
  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'Error desconocido';

    if (error.error instanceof ErrorEvent) {
      // Errores del lado del cliente o problemas de red local
      errorMessage = `Error: ${error.error.message}`;
    } else {
      // Errores específicos del servidor (ej: código inválido o expirado)
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
