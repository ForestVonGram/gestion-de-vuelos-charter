import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../../services/auth/auth.service';
import { CommonModule } from '@angular/common';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { RecaptchaV3Module, ReCaptchaV3Service } from 'ng-recaptcha';
import { RolUsuario } from '../../../models/users/auth.models';
import {AccesibilidadComponent} from '../../../shared/accesibilidad/accesibilidad.component';
import { environment } from '../../../../environments/environment';
import { GoogleAuthService } from '../../../services/auth/google-oauth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, RecaptchaV3Module, RouterModule, AccesibilidadComponent],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit, OnDestroy {
  // --- Propiedades del componente ---
  loginForm!: FormGroup;
  loading = false;
  submitted = false;
  error: string | null = null;
  showPassword = false;
  googleLoading = false;

  // Sujeto para gestionar la desuscripción automática de Observables
  private destroy$ = new Subject<void>();

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private recaptchaV3Service: ReCaptchaV3Service,
    private cdr: ChangeDetectorRef, // Inyección para control manual de renderizado
    private googleAuthService: GoogleAuthService,

  ) {
    // Redirección automática si el usuario ya tiene una sesión activa
    if (this.authService.isAuthenticated()) {
      const currentUser = this.authService.currentUserValue;
      if (currentUser?.rol === RolUsuario.ADMINISTRADOR) {
        this.router.navigate(['/admin/dashboard']);
      } else {
        this.router.navigate(['/dashboard']);
      }
    }
  }

  ngOnInit(): void {
    this.initializeForm(); // Configuración inicial del formulario
    this.initializeGoogleSignIn();
  }

  ngOnDestroy(): void {
    // Limpieza de suscripciones al destruir el componente
    this.destroy$.next();
    this.destroy$.complete();
  }

  // Define la estructura y validaciones del formulario de acceso
  private initializeForm(): void {
    this.loginForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      rememberMe: [false]
    });
  }

  // Getter para facilitar el acceso a los controles del formulario en el HTML
  get f() {
    return this.loginForm.controls;
  }

  // Lógica principal de envío del formulario
  onSubmit(): void {
    this.submitted = true;
    this.error = null;

    // Detener el proceso si el formulario no cumple las validaciones
    if (this.loginForm.invalid) {
      return;
    }

    this.loading = true;
    this.cdr.detectChanges(); // Asegura que el spinner se muestre de inmediato

    // Paso 1: Obtener token de reCAPTCHA v3 para validación de seguridad
    this.recaptchaV3Service.execute('login')
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (token: string) => {
          const loginRequest = {
            email: this.f['email'].value,
            password: this.f['password'].value,
            recaptchaToken: token
          };

          // Paso 2: Intentar el inicio de sesión con el servicio de autenticación
          this.authService.login(loginRequest)
            .pipe(takeUntil(this.destroy$))
            .subscribe({
              next: (response) => {
                this.loading = false;
                this.cdr.detectChanges();

                // Manejo de flujo según la respuesta (2FA o Redirección por Rol)
                // login.component.ts
                if (response.requires2FA) {
                  sessionStorage.setItem('2fa-email', response.email);
                  this.router.navigate(['/auth/verificacion-2fa'], {
                    queryParams: { sessionToken: response.sessionToken }
                  });
                } else {
                  if (response.rol === RolUsuario.ADMINISTRADOR) {
                    this.router.navigate(['/admin/dashboard']);
                  } else {
                    this.router.navigate(['/dashboard']);
                  }
                }
              },
              error: (error) => {
                this.loading = false;
                this.error = this.getErrorMessage(error); // Traducir error del servidor
                this.cdr.detectChanges();
                console.error('Error en login:', error);
              }
            });
        },
        error: (error) => {
          this.loading = false;
          this.error = 'Error en validación de seguridad. Por favor intente nuevamente';
          this.cdr.detectChanges();
          console.error('reCAPTCHA v3 error:', error);
        }
      });
  }

  // Centraliza la lógica de mensajes de error para el usuario final
  private getErrorMessage(error: any): string {

    if (error.error instanceof ErrorEvent) {
      return `Error: ${error.error.message}`;
    }

    if (error.status === 401) {
      return error.error?.message || 'Email o contraseña inválidos';
    } else if (error.status === 0) {
      return 'Error de conexión con el servidor';
    } else if (error.error?.message?.includes('reCAPTCHA')) {
      return 'Validación reCAPTCHA fallida. Por favor intente nuevamente';
    } else {
      return error.error?.message || `Error ${error.status}: ${error.message || 'Error en el login'}`;
    }
  }

  // Alternar visualización de caracteres en el campo de contraseña
  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
    this.cdr.detectChanges();
  }

  // Inicializa el botón de Google Identity Services
  private initializeGoogleSignIn(): void {
    if (typeof (window as any).google === 'undefined') return;

    (window as any).google.accounts.id.initialize({
      client_id: environment.googleClientId,
      callback: (response: any) => this.handleGoogleCallback(response),
      auto_select: false,
      cancel_on_tap_outside: true
    });

    (window as any).google.accounts.id.renderButton(
      document.getElementById('google-signin-button'),
      {
        theme: 'outline',
        size: 'large',
        width: '100%',
        text: 'signin_with',
        locale: 'es'
      }
    );
  }

// Callback invocado por Google con el ID Token
  private handleGoogleCallback(googleResponse: any): void {
    if (!googleResponse?.credential) {
      this.error = 'Error al obtener credenciales de Google';
      this.cdr.detectChanges();
      return;
    }

    this.googleLoading = true;
    this.error = null;
    this.cdr.detectChanges();

    this.authService.loginConGoogle(googleResponse.credential)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.googleLoading = false;
          this.cdr.detectChanges();

          if (response.requires2FA) {
            this.router.navigate(['/auth/verify-2fa'], {
              queryParams: { sessionToken: response.sessionToken }
            });
          } else {
            if (response.rol === RolUsuario.ADMINISTRADOR) {
              this.router.navigate(['/admin/dashboard']);
            } else {
              this.router.navigate(['/dashboard']);
            }
          }
        },
        error: (error) => {
          this.googleLoading = false;
          this.error = 'Error al autenticar con Google. Por favor intente nuevamente';
          this.cdr.detectChanges();
          console.error('Google login error:', error);
        }
      });
  }
}
