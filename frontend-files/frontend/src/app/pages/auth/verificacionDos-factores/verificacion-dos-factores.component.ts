import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Router, ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Subject, interval, takeUntil } from 'rxjs';
import { TwoFactorService } from '../../../services/auth/two-factor.service';
import { AuthService } from '../../../services/auth/auth.service';
import { AccesibilidadComponent } from '../../../shared/accesibilidad/accesibilidad.component';
import { AuthResponse } from '../../../models/users/auth.models';

@Component({
  selector: 'app-verificacion-dos-factores',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, RouterModule, AccesibilidadComponent],
  templateUrl: './verificacion-dos-factores.component.html',
  styleUrls: ['./verificacion-dos-factores.component.css']
})
export class VerificacionDosFactoresComponent implements OnInit, OnDestroy {
  verificationForm!: FormGroup;
  loading = false;
  submitted = false;
  error: string | null = null;

  email: string | null = null;

  sessionToken: string | null = null;
  countdown = 300; // 5 minutos
  private destroy$ = new Subject<void>();

  constructor(
    private fb: FormBuilder,
    private twoFactorService: TwoFactorService,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.sessionToken = this.route.snapshot.queryParamMap.get('sessionToken');
    this.email = sessionStorage.getItem('2fa-email');
    console.log('[2FA] sessionToken obtenido:', this.sessionToken);
    console.log('[2FA] email obtenido:', this.email);
    if (!this.sessionToken || !this.email) {
      this.error = 'Sesión no válida. Por favor, inicia sesión nuevamente.';
      setTimeout(() => this.router.navigate(['/auth/login']), 2000);
      return;
    }
    this.initializeForm();
    this.startCountdown();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private initializeForm(): void {
    this.verificationForm = this.fb.group({
      codigo: ['', [
        Validators.required,
        Validators.pattern('^[0-9]{6}$')
      ]]
    });
  }

  get f() {
    return this.verificationForm.controls;
  }

  onCodeInput(event: any): void {
    let value = event.target.value.replace(/\D/g, '');
    if (value.length > 6) value = value.slice(0, 6);
    this.verificationForm.patchValue({ codigo: value }, { emitEvent: false });

    if (value.length === 6 && !this.loading) {
      console.log('[2FA] Código completo detectado, auto-enviando...');
      this.onSubmit();
    }
  }

  onSubmit(): void {
    this.submitted = true;
    this.error = null;

    if (this.verificationForm.invalid) {
      console.warn('[2FA] Formulario inválido:', this.verificationForm.errors);
      return;
    }

    const codigo = this.verificationForm.get('codigo')?.value;
    console.log('[2FA] Enviando código:', codigo);
    console.log('[2FA] Usando sessionToken:', this.sessionToken);

    this.loading = true;
    this.cdr.detectChanges();

    this.twoFactorService.verificarCodigo(this.sessionToken!, this.email!, codigo)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response: AuthResponse) => {
          console.log('[2FA] Verificación exitosa, respuesta:', response);
          this.loading = false;
          // Guardar sesión con el token de autenticación
          this.authService.handleAuthResponse(response);
          this.cdr.detectChanges();
          // Redirigir según el rol
          if (response.rol === 'ADMINISTRADOR') {
            console.log('[2FA] Redirigiendo a admin/dashboard');
            this.router.navigate(['/admin/dashboard']);
          } else {
            console.log('[2FA] Redirigiendo a dashboard de usuario');
            this.router.navigate(['/dashboard']);
          }
          // Después de redirigir exitosamente
          sessionStorage.removeItem('2fa-email');
        },
        error: (err) => {
          console.error('[2FA] Error en verificación:', err);
          this.loading = false;
          this.error = this.getErrorMessage(err);
          console.log('[2FA] Mensaje de error mostrado:', this.error);
          this.cdr.detectChanges();
        }
      });
  }

  resendCode(event: Event): void {
    event.preventDefault();
    if (this.loading) return;

    console.log('[2FA] Solicitando reenvío de código con sessionToken:', this.sessionToken);
    this.loading = true;
    this.error = null;
    this.cdr.detectChanges();

    this.twoFactorService.reenviarCodigo(this.sessionToken!)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          console.log('[2FA] Código reenviado exitosamente');
          this.loading = false;
          this.countdown = 300;
          this.startCountdown();
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('[2FA] Error al reenviar código:', err);
          this.loading = false;
          this.error = this.getResendErrorMessage(err);
          this.cdr.detectChanges();
        }
      });
  }

  private getErrorMessage(error: any): string {
    console.log('[2FA] Obteniendo mensaje de error para:', error);
    console.log('[2FA] error.error:', error.error);
    if (error.error?.fieldErrors && Array.isArray(error.error.fieldErrors)) {
      const details = error.error.fieldErrors.map((fe: any) => `${fe.field}: ${fe.message}`).join(', ');
      console.log('[2FA] Detalles de validación:', details);
      return `Error de validación: ${details}`;
    }
    if (error.error?.message) {
      return error.error.message;
    }
    if (error.status === 400) return 'El código ingresado es incorrecto.';
    if (error.status === 404) return 'No se encontró una solicitud activa.';
    if (error.status === 429) return 'Demasiados intentos. Espera unos minutos.';
    if (error.status === 0) return 'Error de conexión con el servidor.';
    return 'Error al verificar el código.';
  }

  private getResendErrorMessage(error: any): string {
    if (error.status === 429) return 'Debes esperar antes de solicitar otro código.';
    if (error.status === 0) return 'Error de conexión.';
    return error.error?.message || 'Error al reenviar el código.';
  }

  private startCountdown(): void {
    this.destroy$.next(); // Limpiar suscripción anterior
    interval(1000)
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        if (this.countdown > 0) {
          this.countdown--;
          this.cdr.detectChanges();
        }
      });
  }

  formatTime(seconds: number): string {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  }
}
