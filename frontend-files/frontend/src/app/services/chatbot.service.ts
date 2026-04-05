import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../../environments/environment';

export interface ChatMessage {
  text: string;
  isUser: boolean;
  timestamp: Date;
}

export interface ChatbotResponse {
  reply: string;
  timestamp: string;
  success: boolean;
  errorMessage: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class ChatbotService {
  private apiUrl = environment.apiUrl + '/chatbot';

  constructor(private http: HttpClient) {}

  /**
   * Envía un mensaje al chatbot
   */
  sendMessage(message: string): Observable<ChatbotResponse> {
    // Obtener el token del localStorage
    const token = localStorage.getItem('token');

    if (!token) {
      // Si no hay token, el usuario no está autenticado
      return throwError(() => new Error('Usuario no autenticado'));
    }

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    return this.http.post<ChatbotResponse>(
      `${this.apiUrl}/message`,
      { message },
      { headers }
    ).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Verifica si el servicio está disponible (endpoint público)
   */
  healthCheck(): Observable<any> {
    return this.http.get(`${this.apiUrl}/health`);
  }

  private handleError(error: any): Observable<never> {
    let errorMessage = 'Error desconocido';

    if (error.error instanceof ErrorEvent) {
      errorMessage = error.error.message;
    } else if (error.status === 401) {
      errorMessage = 'Sesión expirada. Por favor inicia sesión nuevamente.';
    } else if (error.status === 0) {
      errorMessage = 'Error de conexión con el servidor';
    } else {
      errorMessage = error.error?.message || `Error ${error.status}`;
    }

    return throwError(() => new Error(errorMessage));
  }
}
