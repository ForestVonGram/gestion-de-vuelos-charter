import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, BehaviorSubject, throwError } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

// Modelos de autenticación
import {
  LoginRequest,
  RegisterRequest,
  AuthResponse,
  RolUsuario, GoogleAuthRequest
} from '../../models/users/auth.models';

export interface User {
  userId: number;
  email: string;
  nombreCompleto: string;
  rol: RolUsuario;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;

  // Manejo del estado del usuario de forma reactiva
  private currentUserSubject: BehaviorSubject<User | null>;
  public currentUser$: Observable<User | null>;

  constructor(private http: HttpClient) {
    // Al recargar la página, intentamos restaurar la sesión desde el LocalStorage
    const storedUser = localStorage.getItem('currentUser');
    this.currentUserSubject = new BehaviorSubject<User | null>(
      storedUser ? JSON.parse(storedUser) : null
    );
    this.currentUser$ = this.currentUserSubject.asObservable();
  }

  // Retorna el valor actual del usuario sin necesidad de suscripción
  public get currentUserValue(): User | null {
    return this.currentUserSubject.value;
  }

  /**
   * Envía las credenciales al backend.
   * Si es exitoso, guarda el token y los datos del usuario.
   */
  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, request).pipe(
      tap(response => this.handleAuthResponse(response)),
      catchError(this.handleError)
    );
  }

  /**
   * Registra un nuevo usuario y, si el backend lo permite,
   * inicia sesión automáticamente tras el registro.
   */
  register(request: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, request).pipe(
      tap(response => {
        if (response.token) {
          this.handleAuthResponse(response);
        }
      }),
      catchError(this.handleError)
    );
  }

  /**
   * Persiste el token y la info del usuario en LocalStorage
   * y actualiza el Stream de datos (BehaviorSubject).
   */
  private handleAuthResponse(response: AuthResponse): void {
    if (response.token) {
      const user: User = {
        userId: response.userId,
        email: response.email,
        nombreCompleto: response.nombreCompleto,
        rol: response.rol
      };

      localStorage.setItem('token', response.token);
      localStorage.setItem('tokenType', response.tokenType || 'Bearer');
      localStorage.setItem('currentUser', JSON.stringify(user));

      // Notifica a todos los componentes suscritos que el usuario cambió
      this.currentUserSubject.next(user);
    }
  }

  /**
   * Centraliza el manejo de errores de red o de credenciales.
   */
  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'Error desconocido';

    if (error.error instanceof ErrorEvent) {
      // Errores del lado del cliente o red
      errorMessage = `Error: ${error.error.message}`;
    } else {
      // Errores retornados por el API (401, 500, etc.)
      if (error.status === 401) {
        errorMessage = error.error?.message || 'Credenciales inválidas';
      } else if (error.status === 0) {
        errorMessage = 'Error de conexión con el servidor';
      } else {
        errorMessage = error.error?.message || `Error ${error.status}: ${error.message}`;
      }
    }

    console.error('AuthService error:', errorMessage);
    return throwError(() => error);
  }

  /**
   * Limpia el almacenamiento y notifica el cierre de sesión (null).
   */
  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('tokenType');
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
  }

  // Helper rápido para verificar si existe un token guardado
  isAuthenticated(): boolean {
    const token = localStorage.getItem('token');
    return !!token;
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  /**
   * Envía el ID Token de Google al backend para autenticación o registro automático.
   * Reutiliza handleAuthResponse para persistir sesión igual que login/register.
   */
  loginConGoogle(credential: string): Observable<AuthResponse> {
    const request: GoogleAuthRequest = { credential };
    return this.http.post<AuthResponse>(`${this.apiUrl}/google`, request).pipe(
      tap(response => this.handleAuthResponse(response)),
      catchError(this.handleError)
    );
  }
}
