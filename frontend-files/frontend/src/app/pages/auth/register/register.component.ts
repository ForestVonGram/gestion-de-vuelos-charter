import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, AbstractControl, ValidationErrors } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { RecaptchaV3Module, ReCaptchaV3Service } from 'ng-recaptcha';
import { AuthService } from '../../../services/auth/auth.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AccesibilidadComponent } from '../../../shared/accesibilidad/accesibilidad.component';
import { environment } from '../../../../environments/environment';
import { RolUsuario } from '../../../models/users/auth.models';
import { GoogleAuthService } from '../../../services/auth/google-oauth.service';

function passwordStrengthValidator(control: AbstractControl): ValidationErrors | null {
  const value: string = control.value || '';
  const hasUppercase = /[A-Z]/.test(value);
  const hasNumber = /[0-9]/.test(value);
  const hasSpecial = /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(value);

  const errors: ValidationErrors = {};
  if (!hasUppercase) errors['noUppercase'] = true;
  if (!hasNumber) errors['noNumber'] = true;
  if (!hasSpecial) errors['noSpecial'] = true;

  return Object.keys(errors).length > 0 ? errors : null;
}

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, RecaptchaV3Module, AccesibilidadComponent],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit, OnDestroy {
  registerForm!: FormGroup;
  isLoading = false;
  errorMessage: string | null = null;
  googleLoading = false;

  passwordStrength: 'none' | 'weak' | 'medium' | 'strong' = 'none';
  passwordStrengthLabel = '';
  strengthChecks = {
    minLength: false,
    hasUppercase: false,
    hasNumber: false,
    hasSpecial: false
  };

  private destroy$ = new Subject<void>();

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private authService: AuthService,
    private recaptchaV3Service: ReCaptchaV3Service,
    private cdr: ChangeDetectorRef,
    private googleAuthService: GoogleAuthService,
  ) {}

  ngOnInit(): void {
    this.initForm(); // Inicializa la estructura del formulario al cargar
    this.initializeGoogleSignIn();
    this.initForm();
    this.registerForm.get('password')?.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe(value => this.evaluatePasswordStrength(value));
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
      telefono: ['', [Validators.pattern(/^[0-9]{7,15}$/)]],
      password: ['', [
        Validators.required,
        Validators.minLength(8),
        passwordStrengthValidator
      ]],
      confirmPassword: ['', [Validators.required]],
      acceptTerms: [false, [Validators.requiredTrue]]
    }, {
      validators: this.passwordMatchValidator
    });
  }

  private passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
    const password = control.get('password');
    const confirmPassword = control.get('confirmPassword');

    if (password && confirmPassword && password.value !== confirmPassword.value) {
      confirmPassword.setErrors({ mismatch: true });
      return { passwordMismatch: true };
    }
    return null;
  }

  private evaluatePasswordStrength(value: string): void {
    this.strengthChecks = {
      minLength: value.length >= 8,
      hasUppercase: /[A-Z]/.test(value),
      hasNumber: /[0-9]/.test(value),
      hasSpecial: /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(value)
    };

    const passed = Object.values(this.strengthChecks).filter(Boolean).length;

    if (!value) {
      this.passwordStrength = 'none';
      this.passwordStrengthLabel = '';
    } else if (passed <= 2) {
      this.passwordStrength = 'weak';
      this.passwordStrengthLabel = 'Débil';
    } else if (passed === 3) {
      this.passwordStrength = 'medium';
      this.passwordStrengthLabel = 'Media';
    } else {
      this.passwordStrength = 'strong';
      this.passwordStrengthLabel = 'Fuerte';
    }
  }

  onSubmit(): void {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    this.errorMessage = null;
    this.cdr.detectChanges();

    this.recaptchaV3Service.execute('register')
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (token: string) => {
          const request = {
            nombre: this.registerForm.get('nombre')?.value,
            apellido: this.registerForm.get('apellido')?.value,
            email: this.registerForm.get('email')?.value,
            password: this.registerForm.get('password')?.value,
            telefono: this.registerForm.get('telefono')?.value || null,
            rol: RolUsuario.USUARIO,
            recaptchaToken: token
          };

          this.authService.register(request)
            .pipe(takeUntil(this.destroy$))
            .subscribe({
              next: () => {
                this.isLoading = false;
                this.cdr.detectChanges();
                this.router.navigate(['/auth/login']);
              },
              error: (err) => {
                this.isLoading = false;
                let errorMsg = '';

                if (err.error) {
                  if (typeof err.error === 'string') {
                    errorMsg = err.error;
                  } else if (err.error.message) {
                    errorMsg = err.error.message;
                  } else if (err.error.error) {
                    errorMsg = err.error.error;
                  }
                } else if (err.message) {
                  errorMsg = err.message;
                }

                console.log('Mensaje de error extraído:', errorMsg);

                if (errorMsg.includes('reCAPTCHA')) {
                  this.errorMessage = 'Validación reCAPTCHA fallida. Por favor intente nuevamente.';
                } else if (errorMsg.includes('El email ya está registrado')) {
                  this.errorMessage = 'El correo electrónico ya está registrado. Por favor utiliza otro.';
                } else if (errorMsg.includes('El telefono ya está registrado') || errorMsg.includes('teléfono ya está registrado')) {
                  this.errorMessage = 'El número de teléfono ya está registrado. Por favor utiliza otro.';
                } else {
                  this.errorMessage = errorMsg || 'Error al registrar usuario. Intente nuevamente.';
                }

                this.cdr.detectChanges();
              }
            });
        },
        error: (error) => {
          this.isLoading = false;
          this.errorMessage = 'Error en validación de seguridad. Por favor intente nuevamente';
          console.error('reCAPTCHA v3 error:', error);
          this.cdr.detectChanges();
        }
      });
  }

  get f() { return this.registerForm.controls; }

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
        text: 'signup_with', // Texto diferente al del login
        locale: 'es'
      }
    );
  }

  private handleGoogleCallback(googleResponse: any): void {
    if (!googleResponse?.credential) {
      this.errorMessage = 'Error al obtener credenciales de Google';
      this.cdr.detectChanges();
      return;
    }

    this.googleLoading = true;
    this.errorMessage = null;
    this.cdr.detectChanges();

    this.authService.loginConGoogle(googleResponse.credential)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.googleLoading = false;
          this.cdr.detectChanges();
          this.router.navigate(['/dashboard']);
        },
        error: (error) => {
          this.googleLoading = false;
          this.errorMessage = 'Error al registrarse con Google. Por favor intente nuevamente';
          this.cdr.detectChanges();
          console.error('Google register error:', error);
        }
      });
  }
}
