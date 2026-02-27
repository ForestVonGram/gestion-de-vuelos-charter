import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class TokenService {

  private TOKEN_KEY = "token";

  constructor() {}

  login(token: string) {
    localStorage.setItem(this.TOKEN_KEY, token);
  }

  logout() {
    localStorage.clear();
  }

  getToken(): string | null {

    return localStorage.getItem(this.TOKEN_KEY);
  }

  isLogged(): boolean {
    return !!this.getToken();
  }

  private decodePayload(token: string): any {
    try {
      const payload = token.split(".")[1];
      const base64 = payload.replace(/-/g, '+').replace(/_/g, '/');
      return JSON.parse(atob(base64));
    } catch {
      return null;
    }
  }

  private getPayload(): any {
    const token = this.getToken();
    return token ? this.decodePayload(token) : null;
  }

  getEmail(): string {
    return this.getPayload()?.sub || "";
  }

  getExpiration(): number {
    return this.getPayload()?.exp || 0;
  }

}