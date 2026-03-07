import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { FormBuilder, FormGroup, Validators, AbstractControl, ValidationErrors, ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Subject, interval, takeUntil } from 'rxjs';
import { PasswordService } from '../../../services/password/password.service';

@Component({
  selector: 'app-recuperar-contrasenia',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, RouterModule],
  templateUrl: './recuperar_contraseña.component.html',
  styleUrls: ['./recuperar_contraseña.component.css']
})
export class RecuperarContraseniaComponent implements OnInit, OnDestroy {
  verificationForm!: FormGroup;
  loading = false;
  submitted = false;
  error: string | null = null;
  email: string = '';
  showNewPassword = false;
  token: string = '';
  codeVerified = false;

  // Password visibility
  showPassword = false;
  showConfirmPassword = false;

  // Countdown
  countdown = 300; // 5 minutos en segundos
  private destroy$ = new Subject<void>();

  constructor(
    private formBuilder: FormBuilder,
    private passwordService: PasswordService,
    private router: Router,
    private cdr: ChangeDetectorRef // Añadimos ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    // Obtener email del sessionStorage
    const savedEmail = sessionStorage.getItem('reset-email');
    if (savedEmail) {
      this.email = savedEmail;
    } else {
      // Si no hay email, redirigir a solicitud
      this.router.navigate(['/auth/recuperar']);
    }

    this.initializeForm();
    this.startCountdown();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private initializeForm(): void {
    this.verificationForm = this.formBuilder.group({
      codigo: ['', [
        Validators.required,
        Validators.pattern('^[0-9]{6}$')
      ]],
      nuevaPassword: ['', []], // Sin validadores inicialmente
      confirmarPassword: ['']
    }, {
      validators: this.passwordMatchValidator
    });
  }

  passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
    const password = control.get('nuevaPassword');
    const confirmPassword = control.get('confirmarPassword');

    if (password && confirmPassword && password.value && confirmPassword.value) {
      if (password.value !== confirmPassword.value) {
        return { passwordMismatch: true };
      }
    }
    return null;
  }

  get f() {
    return this.verificationForm.controls;
  }

  get passwordStrength(): number {
    const password = this.f['nuevaPassword'].value || '';
    let strength = 0;

    if (password.length >= 8) strength++;
    if (password.match(/[a-z]/)) strength++;
    if (password.match(/[A-Z]/)) strength++;
    if (password.match(/[0-9]/)) strength++;
    if (password.match(/[^a-zA-Z0-9]/)) strength++;

    return Math.min(strength, 4);
  }

  getStrengthText(): string {
    const strength = this.passwordStrength;
    if (strength <= 1) return 'Muy débil';
    if (strength === 2) return 'Débil';
    if (strength === 3) return 'Media';
    return 'Fuerte';
  }

  getStrengthClass(): string {
    const strength = this.passwordStrength;
    if (strength <= 1) return 'weak';
    if (strength === 2) return 'weak';
    if (strength === 3) return 'medium';
    return 'strong';
  }

  onCodeInput(event: any): void {
    // Auto-formatear y validar código
    let value = event.target.value.replace(/\D/g, '');
    if (value.length > 6) {
      value = value.slice(0, 6);
    }
    this.verificationForm.patchValue({ codigo: value }, { emitEvent: false });

    // Auto-verificar cuando tenga 6 dígitos
    if (value.length === 6 && !this.showNewPassword) {
      this.verifyCode();
    }
  }

  verifyCode(): void {
    this.submitted = true;
    this.error = null;
    this.codeVerified = false;

    if (this.f['codigo'].invalid) {
      return;
    }

    this.loading = true;
    this.cdr.detectChanges(); // Forzar detección de cambios

    this.passwordService.verificarCodigo(this.email, this.f['codigo'].value)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.loading = false;
          this.token = response.token;
          this.showNewPassword = true;
          this.codeVerified = true;

          // Habilitar validaciones de contraseña
          this.f['nuevaPassword'].setValidators([Validators.required, Validators.minLength(8)]);
          this.f['confirmarPassword'].setValidators([Validators.required]);
          this.f['nuevaPassword'].updateValueAndValidity();
          this.f['confirmarPassword'].updateValueAndValidity();

          this.error = null;
          this.cdr.detectChanges(); // Forzar detección de cambios
        },
        error: (error) => {
          this.loading = false;
          this.codeVerified = false;
          this.error = this.getVerifyCodeErrorMessage(error);
          this.cdr.detectChanges(); // Forzar detección de cambios
          console.error('Error al verificar código:', error);
        }
      });
  }

  private getVerifyCodeErrorMessage(error: any): string {
    if (error.status === 400) {
      return 'Ese no es el código. Intenta de nuevo.';
    } else if (error.status === 0) {
      return 'Error de conexión con el servidor';
    } else if (error.status === 429) {
      return 'Demasiados intentos. Espera unos minutos.';
    } else {
      return error.error?.message || 'Error al verificar el código';
    }
  }

  onSubmit(): void {
    this.submitted = true;
    this.error = null;

    if (!this.showNewPassword) {
      // Primera etapa: verificar código
      this.verifyCode();
      return;
    }

    // Segunda etapa: restablecer contraseña
    if (this.verificationForm.invalid) {
      return;
    }

    this.loading = true;
    this.cdr.detectChanges(); // Forzar detección de cambios

    this.passwordService.resetearPassword(this.token, this.f['nuevaPassword'].value)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.loading = false;
          sessionStorage.removeItem('reset-email');
          this.cdr.detectChanges(); // Forzar detección de cambios
          this.router.navigate(['/auth/login'], {
            queryParams: { reset: 'success' }
          });
        },
        error: (error) => {
          this.loading = false;
          this.error = this.getResetPasswordErrorMessage(error);
          this.cdr.detectChanges(); // Forzar detección de cambios
          console.error('Error al restablecer contraseña:', error);
        }
      });
  }

  private getResetPasswordErrorMessage(error: any): string {
    if (error.status === 0) {
      return 'Error de conexión con el servidor';
    } else if (error.status === 400) {
      return 'El token ha expirado o es inválido. Solicita uno nuevo.';
    } else {
      return error.error?.message || 'Error al restablecer la contraseña';
    }
  }

  resendCode(event: Event): void {
    event.preventDefault();

    this.loading = true;
    this.error = null;
    this.cdr.detectChanges(); // Forzar detección de cambios

    this.passwordService.solicitarRecuperacion(this.email)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.loading = false;
          this.countdown = 300;
          this.startCountdown();
          this.error = null;
          this.cdr.detectChanges(); // Forzar detección de cambios
          console.log('Código reenviado');
        },
        error: () => {
          this.loading = false;
          // Por seguridad, mostrar éxito aunque falle
          this.countdown = 300;
          this.startCountdown();
          this.cdr.detectChanges(); // Forzar detección de cambios
        }
      });
  }

  private startCountdown(): void {
    interval(1000)
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        if (this.countdown > 0) {
          this.countdown--;
          this.cdr.detectChanges(); // Forzar detección de cambios para el contador
        }
      });
  }

  formatTime(seconds: number): string {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  }

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
    this.cdr.detectChanges(); // Forzar detección de cambios
  }

  toggleConfirmPasswordVisibility(): void {
    this.showConfirmPassword = !this.showConfirmPassword;
    this.cdr.detectChanges(); // Forzar detección de cambios
  }
}
