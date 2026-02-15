import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, AbstractControl, ValidationErrors } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { RecaptchaV3Module, ReCaptchaV3Service } from 'ng-recaptcha';
import { AuthService } from '../../../services/auth/auth.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

// Enum simulado basado en tu DTO (ajustalo según tu backend real)
export enum RolUsuario {
  CLIENTE = 'CLIENTE',
  ADMIN = 'ADMIN'
}

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, RecaptchaV3Module],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit, OnDestroy {
  registerForm!: FormGroup;
  isLoading = false;
  errorMessage: string | null = null;
  private destroy$ = new Subject<void>();

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private authService: AuthService,
    private recaptchaV3Service: ReCaptchaV3Service
  ) {}

  ngOnInit(): void {
    this.initForm();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private initForm(): void {
    this.registerForm = this.fb.group({
      nombre: ['', [Validators.required]],
      apellido: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      telefono: ['', [Validators.pattern(/^[0-9]{7,15}$/)]], // Patrón según DTO backend
      password: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', [Validators.required]],
      acceptTerms: [false, [Validators.requiredTrue]]
    }, { validators: this.passwordMatchValidator });
  }

  // Validador personalizado para comparar contraseñas
  private passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
    const password = control.get('password');
    const confirmPassword = control.get('confirmPassword');

    if (password && confirmPassword && password.value !== confirmPassword.value) {
      confirmPassword.setErrors({ mismatch: true });
      return { passwordMismatch: true };
    }
    return null;
  }

  onSubmit(): void {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    this.errorMessage = null;

    // Ejecutar reCAPTCHA v3
    this.recaptchaV3Service.execute('register')
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (token: string) => {
          // Construcción del objeto basado en RegisterRequest
          const request = {
            nombre: this.registerForm.get('nombre')?.value,
            apellido: this.registerForm.get('apellido')?.value,
            email: this.registerForm.get('email')?.value,
            password: this.registerForm.get('password')?.value,
            telefono: this.registerForm.get('telefono')?.value || null,
            rol: RolUsuario.CLIENTE,
            recaptchaToken: token
          };

          this.authService.register(request)
            .pipe(takeUntil(this.destroy$))
            .subscribe({
              next: (response) => {
                this.isLoading = false;
                this.router.navigate(['/auth/login']);
              },
              error: (err) => {
                this.isLoading = false;
                if (err.error?.message?.includes('reCAPTCHA')) {
                  this.errorMessage = 'Validación reCAPTCHA fallida. Por favor intente nuevamente.';
                } else if (err.error?.message?.includes('email')) {
                  this.errorMessage = 'El email ya está registrado.';
                } else {
                  this.errorMessage = err.error?.message || 'Error al registrar usuario. Intente nuevamente.';
                }
              }
            });
        },
        error: (error) => {
          this.isLoading = false;
          this.errorMessage = 'Error en validación de seguridad. Por favor intente nuevamente';
          console.error('reCAPTCHA v3 error:', error);
        }
      });
  }

  // Getters para facilitar el acceso en el HTML
  get f() { return this.registerForm.controls; }
}
