import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, AbstractControl, ValidationErrors } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { RecaptchaV3Module, ReCaptchaV3Service } from 'ng-recaptcha';
import { AuthService } from '../../../services/auth/auth.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import {AccesibilidadComponent} from '../../../shared/accesibilidad/accesibilidad.component';

// Definición de roles disponibles para el registro de nuevos usuarios
export enum RolUsuario {
  USUARIO = 'USUARIO',
  ADMINISTRADOR = 'ADMINISTRADOR'
}

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, RecaptchaV3Module, AccesibilidadComponent],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit, OnDestroy {
  // --- Propiedades de estado ---
  registerForm!: FormGroup;
  isLoading = false;
  errorMessage: string | null = null;

  // Manejador para cancelar suscripciones activas al destruir el componente
  private destroy$ = new Subject<void>();

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private authService: AuthService,
    private recaptchaV3Service: ReCaptchaV3Service
  ) {}

  ngOnInit(): void {
    this.initForm(); // Inicializa la estructura del formulario al cargar
  }

  ngOnDestroy(): void {
    // Limpieza de observables para evitar fugas de memoria
    this.destroy$.next();
    this.destroy$.complete();
  }

  // Configuración del formulario reactivo con sus respectivas validaciones
  private initForm(): void {
    this.registerForm = this.fb.group({
      nombre: ['', [Validators.required]],
      apellido: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      // Validación de teléfono: solo números, entre 7 y 15 dígitos
      telefono: ['', [Validators.pattern(/^[0-9]{7,15}$/)]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', [Validators.required]],
      // Obliga a que los términos y condiciones estén marcados
      acceptTerms: [false, [Validators.requiredTrue]]
    }, {
      // Validador de grupo para asegurar que ambas contraseñas coincidan
      validators: this.passwordMatchValidator
    });
  }

  // Validador personalizado: Compara 'password' y 'confirmPassword'
  private passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
    const password = control.get('password');
    const confirmPassword = control.get('confirmPassword');

    if (password && confirmPassword && password.value !== confirmPassword.value) {
      // Setea el error directamente en el campo de confirmación
      confirmPassword.setErrors({ mismatch: true });
      return { passwordMismatch: true };
    }
    return null;
  }

  // Lógica de procesamiento del registro
  onSubmit(): void {
    // Si el formulario no es válido, marca campos para mostrar errores visuales
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    this.errorMessage = null;

    // Paso 1: Ejecutar validación invisible de reCAPTCHA v3
    this.recaptchaV3Service.execute('register')
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (token: string) => {
          // Paso 2: Mapear datos del formulario al objeto de petición (Request)
          const request = {
            nombre: this.registerForm.get('nombre')?.value,
            apellido: this.registerForm.get('apellido')?.value,
            email: this.registerForm.get('email')?.value,
            password: this.registerForm.get('password')?.value,
            telefono: this.registerForm.get('telefono')?.value || null,
            rol: RolUsuario.USUARIO, // Por defecto se registra como rol estándar
            recaptchaToken: token
          };

          // Paso 3: Llamada al servicio de autenticación para el registro
          this.authService.register(request)
            .pipe(takeUntil(this.destroy$))
            .subscribe({
              next: (response) => {
                this.isLoading = false;
                // Si el registro es exitoso, redirige al login
                this.router.navigate(['/auth/login']);
              },
              error: (err) => {
                this.isLoading = false;
                // Manejo de errores específicos del servidor (email duplicado, captcha, etc.)
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

  // Getter de conveniencia para acceder a los campos en el template HTML
  get f() { return this.registerForm.controls; }
}
