import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { FormBuilder, FormGroup, Validators, AbstractControl, ValidationErrors, ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Subject, interval, takeUntil } from 'rxjs';
import { PasswordService } from '../../../services/password/password.service';
import { AccesibilidadComponent } from '../../../shared/accesibilidad/accesibilidad.component';

/**
 * Componente para la recuperación de contraseña.
 * Maneja el flujo completo: verificación de código y establecimiento de nueva contraseña.
 */
@Component({
  selector: 'app-recuperar-contrasenia',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, RouterModule, AccesibilidadComponent],
  templateUrl: './recuperar_contraseña.component.html',
  styleUrls: ['./recuperar_contraseña.component.css']
})
export class RecuperarContraseniaComponent implements OnInit, OnDestroy {

  // Formulario reactivo
  verificationForm!: FormGroup;

  // Estados de la UI
  loading = false; // Indicador de carga
  submitted = false; // Indica si el formulario ha sido enviado
  error: string | null = null; // Mensaje de error

  // Datos del proceso
  email: string = ''; // Email del usuario
  showNewPassword = false; // Muestra el formulario de nueva contraseña
  token: string = ''; // Token de verificación
  codeVerified = false; // Indica si el código fue verificado

  // Visibilidad de contraseñas
  showPassword = false;
  showConfirmPassword = false;

  // Contador regresivo para reenvío de código
  countdown = 300; // 5 minutos en segundos
  private destroy$ = new Subject<void>(); // Subject para limpiar suscripciones

  /**
   * Constructor del componente
   * @param formBuilder FormBuilder para crear formularios reactivos
   * @param passwordService servicio para operaciones de contraseña
   * @param router servicio de navegación
   * @param cdr ChangeDetectorRef para forzar detección de cambios
   */
  constructor(
    private formBuilder: FormBuilder,
    private passwordService: PasswordService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  /**
   * Inicialización del componente.
   * Obtiene el email guardado y configura el formulario.
   */
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

  /**
   * Limpieza al destruir el componente.
   */
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * Inicializa el formulario con sus campos y validaciones.
   */
  private initializeForm(): void {
    this.verificationForm = this.formBuilder.group({
      codigo: ['', [
        Validators.required,
        Validators.pattern('^[0-9]{6}$') // Solo números de 6 dígitos
      ]],
      nuevaPassword: ['', []], // Sin validadores inicialmente
      confirmarPassword: ['']
    }, {
      validators: this.passwordMatchValidator // Validador personalizado
    });
  }

  /**
   * Validador personalizado que verifica que las contraseñas coincidan.
   * @param control grupo de controles del formulario
   * @returns error si no coinciden, null si son iguales
   */
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

  /**
   * Getter para acceder fácilmente a los controles del formulario.
   */
  get f() {
    return this.verificationForm.controls;
  }

  /**
   * Calcula la fortaleza de la contraseña.
   * @returns número del 0 al 4 indicando fortaleza
   */
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

  /**
   * Obtiene el texto descriptivo de la fortaleza de contraseña.
   * @returns texto de fortaleza
   */
  getStrengthText(): string {
    const strength = this.passwordStrength;
    if (strength <= 1) return 'Muy débil';
    if (strength === 2) return 'Débil';
    if (strength === 3) return 'Media';
    return 'Fuerte';
  }

  /**
   * Obtiene la clase CSS para la barra de fortaleza.
   * @returns clase CSS
   */
  getStrengthClass(): string {
    const strength = this.passwordStrength;
    if (strength <= 1) return 'weak';
    if (strength === 2) return 'weak';
    if (strength === 3) return 'medium';
    return 'strong';
  }

  /**
   * Maneja la entrada del código de verificación.
   * Auto-formatea y valida el código.
   * @param evento del input
   */
  onCodeInput(event: any): void {
    // Auto-formatear y validar código
    let value = event.target.value.replace(/\D/g, ''); // Solo números
    if (value.length > 6) {
      value = value.slice(0, 6); // Limitar a 6 dígitos
    }
    this.verificationForm.patchValue({ codigo: value }, { emitEvent: false });

    // Auto-verificar cuando tenga 6 dígitos
    if (value.length === 6 && !this.showNewPassword) {
      this.verifyCode();
    }
  }

  /**
   * Verifica el código de verificación con el backend.
   */
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

  /**
   * Obtiene mensaje de error personalizado para verificación de código.
   * @param error objeto de error
   * @returns mensaje descriptivo
   */
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

  /**
   * Maneja el envío del formulario.
   * Verifica código o restablece contraseña según el estado.
   */
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

  /**
   * Obtiene mensaje de error personalizado para restablecimiento de contraseña.
   * @param error objeto de error
   * @returns mensaje descriptivo
   */
  private getResetPasswordErrorMessage(error: any): string {
    if (error.status === 0) {
      return 'Error de conexión con el servidor';
    } else if (error.status === 400) {
      return 'El token ha expirado o es inválido. Solicita uno nuevo.';
    } else {
      return error.error?.message || 'Error al restablecer la contraseña';
    }
  }

  /**
   * Reenvía el código de verificación al email.
   * @param evento del clic
   */
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

  /**
   * Inicia el contador regresivo para reenvío de código.
   */
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

  /**
   * Formatea los segundos a formato mm:ss.
   * @param segundos totales
   * @returns string formateado
   */
  formatTime(seconds: number): string {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  }

  /**
   * Alterna la visibilidad del campo de contraseña.
   */
  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
    this.cdr.detectChanges(); // Forzar detección de cambios
  }

  /**
   * Alterna la visibilidad del campo de confirmación de contraseña.
   */
  toggleConfirmPasswordVisibility(): void {
    this.showConfirmPassword = !this.showConfirmPassword;
    this.cdr.detectChanges(); // Forzar detección de cambios
  }
}
