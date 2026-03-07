import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { environment } from '../../../environments/environment'; // Importamos environment
import { ProfileDto } from '../../models/users/profile-dto';
import { User } from './auth.service'; // Importamos User desde AuthService para mantener consistencia en el tipo de usuario
import { TokenService } from '../token_service/token-service'; // Importamos TokenService para manejar el token de autenticación
import { HttpHeaders } from '@angular/common/http'; // Importamos HttpHeaders para agregar el token en las solicitudes HTTP

@Injectable({
  providedIn: 'root',
})
export class ProfileService {
  private readonly API_URL = `${environment.apiUrl}/tripulantes`;
  private currentUserSubject: BehaviorSubject<User | null>;
  public currentUser$: Observable<User | null> | undefined;

  constructor(private http: HttpClient, private tokenService: TokenService) {
    const storedUser = localStorage.getItem('currentUser');
    this.currentUserSubject = new BehaviorSubject<User | null>(
      storedUser ? JSON.parse(storedUser) : null
    );
    this.currentUser$ = this.currentUserSubject.asObservable();
  }

  public get currentUserValue(): User | null {
    return this.currentUserSubject.value;
  }

  getProfile(id: number): Observable<ProfileDto> {

    const token= this.tokenService.getToken();

    const headers = new HttpHeaders({
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
    });

    return this.http.get<ProfileDto>(`${this.API_URL}/${id}`, { headers });
  }


}
