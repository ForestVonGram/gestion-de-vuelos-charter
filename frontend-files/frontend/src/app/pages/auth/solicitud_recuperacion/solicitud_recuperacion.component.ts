import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { PasswordService } from '../../../services/password/password.service';

@Component({
  selector: 'app-solicitud-recuperacion',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, RouterModule],
  templateUrl: './solicitud_recuperacion.component.html',
  styleUrls: ['./solicitud_recuperacion.component.css']
})
export class RecuperarSolicitudComponent implements OnInit, OnDestroy {
  forgotForm!: FormGroup;
  loading = false;
  submitted = false;
  success = false;
  error: string | null = null;
  private destroy$ = new Subject<void>();

  constructor(
    private formBuilder: FormBuilder,
    private passwordService: PasswordService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.initializeForm();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private initializeForm(): void {
    this.forgotForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]]
    });
  }

  get f() {
    return this.forgotForm.controls;
  }

  onSubmit(): void {
    this.submitted = true;
    this.error = null;

    if (this.forgotForm.invalid) {
      return;
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
            this.success = true;
            setTimeout(() => {
              this.router.navigate(['/auth/recuperar/verificar']);
            }, 3000);
          }
        }
      });
  }

  resendCode(event: Event): void {
    event.preventDefault();
    if (this.f['email'].value) {
      this.onSubmit();
    }
  }
}
