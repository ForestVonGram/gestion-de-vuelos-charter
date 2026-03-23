import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthResponse } from '../../models/users/auth.models';

@Injectable({
  providedIn: 'root'
})
export class TwoFactorService {
  private apiUrl = `${environment.apiUrl}/auth`; // Sin /2fa

  constructor(private http: HttpClient) {}

  /**
   * Verifica el código de dos factores usando el endpoint existente.
   * @param sessionToken Token temporal de sesión
   * @param codigo Código de 6 dígitos
   */
// two-factor.service.ts
  verificarCodigo(sessionToken: string, email: string, codigo: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/verify-2fa`, { sessionToken, email, codigo });
  }

  /**
   * Reenvía el código de dos factores.
   * Este endpoint aún no existe; debes crearlo en el backend.
   * Mientras tanto, puedes comentarlo o simularlo.
   */
  reenviarCodigo(sessionToken: string): Observable<void> {
    // Si implementas el endpoint, usa esta línea:
    // return this.http.post<void>(`${this.apiUrl}/resend-2fa`, { sessionToken });

    // Mientras tanto, para pruebas, lanzamos un error indicando que falta implementar.
    throw new Error('Endpoint de reenvío no implementado en el backend. Por favor, agrega POST /api/auth/resend-2fa');
  }
}
