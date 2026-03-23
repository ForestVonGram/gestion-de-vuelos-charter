import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ProfileDto } from '../../models/users/profile-dto';
import { User } from './auth.service';
import { TokenService } from '../token_service/token-service';

@Injectable({
  providedIn: 'root',
})
export class ProfileService {
  // URL base consumida desde las variables de entorno
  private readonly API_URL = `${environment.apiUrl}/tripulantes`;

  // Sujeto que mantiene el estado actual del usuario y permite a otros componentes "escuchar" cambios
  private currentUserSubject: BehaviorSubject<User | null>;

  // Observable público para que los componentes se suscriban de forma reactiva
  public currentUser$: Observable<User | null> | undefined;

  constructor(private http: HttpClient, private tokenService: TokenService) {
    // Al instanciar el servicio, intentamos recuperar el usuario guardado en el navegador
    const storedUser = localStorage.getItem('currentUser');
    this.currentUserSubject = new BehaviorSubject<User | null>(
      storedUser ? JSON.parse(storedUser) : null
    );
    this.currentUser$ = this.currentUserSubject.asObservable();
  }

  /**
   * Getter para obtener el valor instantáneo del usuario actual
   * sin necesidad de suscribirse al observable.
   */
  public get currentUserValue(): User | null {
    return this.currentUserSubject.value;
  }

  /**
   * Obtiene la información detallada del perfil de un tripulante por su ID.
   * Incluye la lógica de adjuntar el token JWT para autorización.
   */
  getProfile(id: number): Observable<ProfileDto> {
    // Recuperamos el token de sesión desde el TokenService
    const token = this.tokenService.getToken();

    // Configuramos las cabeceras HTTP necesarias para que el Backend acepte la petición
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    // Petición GET tipada con ProfileDto
    return this.http.get<ProfileDto>(`${this.API_URL}/${id}`, { headers });
  }
}
