import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class TokenService {

  // Clave única para identificar el token en el almacenamiento del navegador
  private TOKEN_KEY = "token";

  constructor() {}

  /**
   * Guarda el token recibido tras un inicio de sesión exitoso.
   */
  login(token: string) {
    localStorage.setItem(this.TOKEN_KEY, token);
  }

  /**
   * Limpia todo el almacenamiento local (Logout), eliminando sesiones previas.
   */
  logout() {
    localStorage.clear();
  }

  /**
   * Recupera el token guardado. Retorna null si no existe.
   */
  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  /**
   * Verifica de forma rápida si hay un usuario autenticado basándose en la existencia del token.
   */
  isLogged(): boolean {
    return !!this.getToken(); // Convierte el valor a booleano
  }

  /**
   * Método privado para decodificar la parte central (Payload) del JWT.
   * Transforma el formato Base64Url a un objeto JSON legible.
   */
  private decodePayload(token: string): any {
    try {
      // Un JWT tiene 3 partes separadas por puntos: Header.Payload.Signature
      const payload = token.split(".")[1];
      // Ajuste de caracteres especiales de Base64 para compatibilidad con atob()
      const base64 = payload.replace(/-/g, '+').replace(/_/g, '/');
      return JSON.parse(atob(base64));
    } catch {
      return null;
    }
  }

  /**
   * Obtiene el objeto de datos contenido dentro del token actual.
   */
  private getPayload(): any {
    const token = this.getToken();
    return token ? this.decodePayload(token) : null;
  }

  /**
   * Extrae el email (sujeto) del token decodificado.
   */
  getEmail(): string {
    return this.getPayload()?.sub || "";
  }

  /**
   * Obtiene la marca de tiempo (Timestamp) de expiración del token.
   * Útil para validar si la sesión sigue vigente antes de una petición.
   */
  getExpiration(): number {
    return this.getPayload()?.exp || 0;
  }
}
