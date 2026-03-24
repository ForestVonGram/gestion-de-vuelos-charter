import { Injectable, NgZone } from '@angular/core';
import { environment } from '../../../environments/environment';

declare global {
  interface Window {
    google: any;
  }
}

@Injectable({
  providedIn: 'root'
})
export class GoogleAuthService {
  private scriptLoaded = false;
  private scriptLoading: Promise<void> | null = null;
  private googleInitialized = false;
  private currentCallbacks: { onSuccess: (response: any) => void; onError?: () => void } | null = null;

  constructor(private ngZone: NgZone) {}

  loadScript(): Promise<void> {
    if (this.scriptLoaded) return Promise.resolve();
    if (this.scriptLoading) return this.scriptLoading;

    this.scriptLoading = new Promise((resolve, reject) => {
      this.ngZone.runOutsideAngular(() => {
        const script = document.createElement('script');
        script.src = 'https://accounts.google.com/gsi/client';
        script.async = true;
        script.defer = true;

        script.onload = () => {
          this.scriptLoaded = true;
          this.scriptLoading = null;
          this.ngZone.run(() => resolve());
        };

        script.onerror = (error) => {
          this.scriptLoading = null;
          this.ngZone.run(() => reject(error));
        };

        document.head.appendChild(script);
      });
    });

    return this.scriptLoading;
  }

  private initializeGoogle(): void {
    if (this.googleInitialized || !window.google) return;

    window.google.accounts.id.initialize({
      client_id: environment.googleClientId,
      callback: (response: any) => {
        if (this.currentCallbacks?.onSuccess) {
          this.ngZone.run(() => this.currentCallbacks!.onSuccess(response));
        }
      },
      error_callback: () => {
        if (this.currentCallbacks?.onError) {
          this.ngZone.run(() => this.currentCallbacks!.onError!());
        }
      }
    });

    this.googleInitialized = true;
  }

  async initializeSignInButton(
    elementId: string,
    onSuccess: (response: any) => void,
    onError?: () => void,
    buttonText: 'signin_with' | 'signup_with' = 'signin_with'
  ): Promise<void> {
    await this.loadScript();

    this.ngZone.runOutsideAngular(() => {
      if (!window.google) {
        console.warn('Google no disponible');
        return;
      }

      this.currentCallbacks = { onSuccess, onError };
      this.initializeGoogle();

      const element = document.getElementById(elementId);

      if (element) {
        element.innerHTML = '';
        setTimeout(() => {                                    // ← aquí
          window.google.accounts.id.renderButton(element, {
            type: 'standard',
            size: 'large',
            theme: 'outline',
            text: buttonText,
            locale: 'es',
            width: element.offsetWidth || 300
          });
        }, 100);
      } else {
        console.warn('Elemento no encontrado:', elementId);
      }
    });
  }

  getIdToken(response: any): string {
    return response?.credential ?? '';
  }
}
