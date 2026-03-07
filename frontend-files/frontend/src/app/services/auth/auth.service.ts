import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, tap } from 'rxjs';
import { environment } from '../../../environments/environment'; // Importamos environment

// 1. IMPORTAMOS LOS MODELOS (En lugar de escribirlos aquí)
import {
  LoginRequest,
  RegisterRequest,
  AuthResponse,
  RolUsuario
} from '../../models/users/auth.models';

// Podemos definir un User simplificado para el frontend si la AuthResponse es muy compleja,
// o usar la misma interfaz si te sirve.
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
  private currentUserSubject: BehaviorSubject<User | null>;
  public currentUser$: Observable<User | null>;

  constructor(private http: HttpClient) {
    // Recuperar usuario al recargar página
    const storedUser = localStorage.getItem('currentUser');
    this.currentUserSubject = new BehaviorSubject<User | null>(
      storedUser ? JSON.parse(storedUser) : null
    );
    this.currentUser$ = this.currentUserSubject.asObservable();
  }

  public get currentUserValue(): User | null {
    return this.currentUserSubject.value;
  }

  // 3. Usamos los tipos importados
  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, request)
      .pipe(
        tap(response => this.handleAuthResponse(response))
      );
  }

  register(request: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, request)
      .pipe(
        tap(response => {
          // Opcional: Si el registro loguea automáticamente, guarda sesión.
          // Si no, quizás no quieras llamar a handleAuthResponse aquí.
          // Depende de si tu backend devuelve token al registrarse.
          this.handleAuthResponse(response);
        })
      );
  }

  private handleAuthResponse(response: AuthResponse): void {
    const user: User = {
      userId: response.userId,
      email: response.email,
      nombreCompleto: response.nombreCompleto,
      rol: response.rol
    };

    if (response.token) {
      localStorage.setItem('token', response.token);
      localStorage.setItem('tokenType', response.tokenType);
      localStorage.setItem('currentUser', JSON.stringify(user));
      this.currentUserSubject.next(user);
    }
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('tokenType');
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
  }

  isAuthenticated(): boolean {
    const token = localStorage.getItem('token');
    // Aquí podrías agregar lógica para verificar si el token expiró (jwt-decode)
    return !!token;
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }
}
