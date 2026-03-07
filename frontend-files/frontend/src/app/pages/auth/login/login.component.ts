import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../../services/auth/auth.service';
import { CommonModule } from '@angular/common';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { RecaptchaV3Module, ReCaptchaV3Service } from 'ng-recaptcha';
import { RolUsuario } from '../../../models/users/auth.models';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, RecaptchaV3Module, RouterModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit, OnDestroy {
  loginForm!: FormGroup;
  loading = false;
  submitted = false;
  error: string | null = null;
  showPassword = false;
  private destroy$ = new Subject<void>();

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private recaptchaV3Service: ReCaptchaV3Service,
    private cdr: ChangeDetectorRef // Añadimos ChangeDetectorRef
  ) {
    // Verificar si ya está autenticado
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
    this.initializeForm();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private initializeForm(): void {
    this.loginForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      rememberMe: [false]
    });
  }

  get f() {
    return this.loginForm.controls;
  }

  onSubmit(): void {
    this.submitted = true;
    this.error = null;

    if (this.loginForm.invalid) {
      return;
    }

    this.loading = true;
    this.cdr.detectChanges(); // Forzar detección de cambios

    // Ejecutar reCAPTCHA v3
    this.recaptchaV3Service.execute('login')
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (token: string) => {
          const loginRequest = {
            email: this.f['email'].value,
            password: this.f['password'].value,
            recaptchaToken: token
          };

          this.authService.login(loginRequest)
            .pipe(takeUntil(this.destroy$))
            .subscribe({
              next: (response) => {
                this.loading = false;
                this.cdr.detectChanges(); // Forzar detección de cambios

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
                this.loading = false;
                this.error = this.getErrorMessage(error);
                this.cdr.detectChanges(); // Forzar detección de cambios
                console.error('Error en login:', error);
              }
            });
        },
        error: (error) => {
          this.loading = false;
          this.error = 'Error en validación de seguridad. Por favor intente nuevamente';
          this.cdr.detectChanges(); // Forzar detección de cambios
          console.error('reCAPTCHA v3 error:', error);
        }
      });
  }

  private getErrorMessage(error: any): string {
    if (error.status === 401) {
      return 'Email o contraseña inválidos';
    } else if (error.status === 0) {
      return 'Error de conexión con el servidor';
    } else if (error.error?.message?.includes('reCAPTCHA')) {
      return 'Validación reCAPTCHA fallida. Por favor intente nuevamente';
    } else {
      return error.error?.message || 'Error en el login';
    }
  }

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
    this.cdr.detectChanges(); // Forzar detección de cambios
  }
}
