import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { PasswordService } from '../../../services/password/password.service';

/**
 * Componente para la solicitud de recuperación de contraseña.
 * Permite al usuario ingresar su email para recibir un código de verificación.
 */
@Component({
  selector: 'app-solicitud-recuperacion',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, RouterModule],
  templateUrl: './solicitud_recuperacion.component.html',
  styleUrls: ['./solicitud_recuperacion.component.css']
})
export class RecuperarSolicitudComponent implements OnInit, OnDestroy {

  // Formulario reactivo
  forgotForm!: FormGroup;

  // Estados de la UI
  loading = false; // Indicador de carga
  submitted = false; // Indica si el formulario ha sido enviado
  success = false; // Indica si la solicitud fue exitosa
  error: string | null = null; // Mensaje de error

  private destroy$ = new Subject<void>(); // Subject para limpiar suscripciones

  /**
   * Constructor del componente
   * @param formBuilder FormBuilder para crear formularios reactivos
   * @param passwordService servicio para operaciones de contraseña
   * @param router servicio de navegación
   */
  constructor(
    private formBuilder: FormBuilder,
    private passwordService: PasswordService,
    private router: Router
  ) {}

  /**
   * Inicialización del componente.
   * Configura el formulario con sus validaciones.
   */
  ngOnInit(): void {
    this.initializeForm();
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
    this.forgotForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]] // Email válido y obligatorio
    });
  }

  /**
   * Getter para acceder fácilmente a los controles del formulario.
   */
  get f() {
    return this.forgotForm.controls;
  }

  /**
   * Maneja el envío del formulario.
   * Envía solicitud de recuperación al backend.
   */
  onSubmit(): void {
    this.submitted = true;
    this.error = null;

    if (this.forgotForm.invalid) {
      return; // No enviar si el formulario es inválido
    }

    this.loading = true;

    this.passwordService.solicitarRecuperacion(this.f['email'].value)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.success = true;
          this.loading = false;

          // Guardar email para la siguiente página
          sessionStorage.setItem('reset-email', this.f['email'].value);

          // Redirigir a la página de verificación después de 3 segundos
          setTimeout(() => {
            this.router.navigate(['/auth/recuperar/verificar']);
          }, 3000);
        },
        error: (error) => {
          this.loading = false;
          if (error.status === 0) {
            this.error = 'Error de conexión con el servidor';
          } else {
            // Por seguridad, mostramos éxito aunque haya error
            // Esto evita que un atacante pueda verificar qué emails existen
            this.success = true;
            setTimeout(() => {
              this.router.navigate(['/auth/recuperar/verificar']);
            }, 3000);
          }
        }
      });
  }

  /**
   * Reenvía el código de verificación.
   * @param evento del clic
   */
  resendCode(event: Event): void {
    event.preventDefault();
    if (this.f['email'].value) {
      this.onSubmit(); // Reutiliza el mismo método de envío
    }
  }
}
