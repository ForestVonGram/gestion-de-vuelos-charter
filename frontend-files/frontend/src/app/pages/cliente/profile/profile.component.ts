import { Component, OnInit, ChangeDetectorRef, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators, AbstractControl, ValidationErrors, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { UserService, Usuario } from '../../../services/user/user.service';
import { AuthService } from '../../../services/auth/auth.service';
import { finalize } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AccesibilidadComponent } from '../../../shared/accesibilidad/accesibilidad.component';

// Validador personalizado para fortaleza de contraseña
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
  selector: 'app-profile',
  standalone: true,
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css'],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,
    AccesibilidadComponent
  ]
})
export class ProfileComponent implements OnInit, OnDestroy {
  profileForm!: FormGroup;
  passwordForm!: FormGroup;
  user: Usuario | null = null;
  isLoading = false;
  errorMessage = '';
  successMessage = '';
  isDarkMode = false;
  isNavbarScrolled = false;

  twoFactorEnabled = false;
  twoFactorMethod: 'EMAIL' | 'SMS' = 'EMAIL';

  // Para la fortaleza de contraseña
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
    private userService: UserService,
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.initForms();
    this.loadUserData();
    window.addEventListener('scroll', this.onWindowScroll.bind(this));
    this.isDarkMode = localStorage.getItem('theme') === 'dark';
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    window.removeEventListener('scroll', this.onWindowScroll.bind(this));
  }

  initForms(): void {
    // Formulario de datos básicos
    this.profileForm = this.fb.group({
      nombre: ['', Validators.required],
      apellido: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      telefono: ['']
    });

    // Formulario de cambio de contraseña con validadores mejorados
    this.passwordForm = this.fb.group({
      nuevaPassword: ['', [
        Validators.required,
        Validators.minLength(8),
        passwordStrengthValidator
      ]],
      confirmarPassword: ['', Validators.required]
    }, { validators: this.passwordMatchValidator });

    // Escuchar cambios en nuevaPassword para actualizar la barra de fortaleza
    this.passwordForm.get('nuevaPassword')?.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe(value => this.evaluatePasswordStrength(value));
  }

  passwordMatchValidator(g: FormGroup): { [key: string]: boolean } | null {
    const nueva = g.get('nuevaPassword')?.value;
    const confirm = g.get('confirmarPassword')?.value;
    return nueva === confirm ? null : { mismatch: true };
  }

  private evaluatePasswordStrength(value: string): void {
    if (!value) {
      this.passwordStrength = 'none';
      this.passwordStrengthLabel = '';
      this.strengthChecks = {
        minLength: false,
        hasUppercase: false,
        hasNumber: false,
        hasSpecial: false
      };
      return;
    }

    this.strengthChecks = {
      minLength: value.length >= 8,
      hasUppercase: /[A-Z]/.test(value),
      hasNumber: /[0-9]/.test(value),
      hasSpecial: /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(value)
    };

    const passed = Object.values(this.strengthChecks).filter(Boolean).length;

    if (passed <= 2) {
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

  loadUserData(): void {
    const currentUser = this.authService.currentUserValue;
    if (!currentUser || !currentUser.userId) {
      this.errorMessage = 'No se pudo identificar al usuario.';
      this.cdr.detectChanges();
      return;
    }
    const userId = currentUser.userId;
    this.isLoading = true;
    this.userService.getUser(userId)
      .pipe(finalize(() => {
        this.isLoading = false;
        this.cdr.detectChanges();
      }))
      .subscribe({
        next: (user: Usuario) => {
          this.user = user;
          this.profileForm.patchValue({
            nombre: user.nombre,
            apellido: user.apellido,
            email: user.email,
            telefono: user.telefono
          });
          this.twoFactorEnabled = user.dosFactoresHabilitado ?? false;
          if (user.metodoDosFactores) {
            this.twoFactorMethod = user.metodoDosFactores;
          }
          this.cdr.detectChanges();
        },
        error: (err: any) => {
          this.errorMessage = 'Error al cargar los datos del perfil.';
          console.error(err);
          this.cdr.detectChanges();
        }
      });
  }

  updateProfile(): void {
    if (!this.user) {
      this.errorMessage = 'Usuario no cargado.';
      this.cdr.detectChanges();
      return;
    }
    if (this.profileForm.invalid) {
      this.errorMessage = 'Por favor completa los campos correctamente.';
      this.cdr.detectChanges();
      return;
    }

    const updateData = this.profileForm.value;
    this.isLoading = true;
    this.userService.updateUser(this.user.id, updateData)
      .pipe(finalize(() => {
        this.isLoading = false;
        this.cdr.detectChanges();
      }))
      .subscribe({
        next: (updatedUser: Usuario) => {
          this.user = updatedUser;
          this.successMessage = 'Perfil actualizado correctamente.';
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (err: any) => {
          this.errorMessage = 'Error al actualizar el perfil.';
          console.error(err);
        }
      });
  }

  changePassword(): void {
    if (!this.user) {
      this.errorMessage = 'Usuario no cargado.';
      this.cdr.detectChanges();
      return;
    }

    // Verificar errores del formulario de contraseña
    if (this.passwordForm.invalid) {
      const passwordCtrl = this.passwordForm.get('nuevaPassword');
      if (passwordCtrl?.errors) {
        if (passwordCtrl.errors['required']) {
          this.errorMessage = 'La contraseña es obligatoria.';
        } else if (passwordCtrl.errors['minlength']) {
          this.errorMessage = 'La contraseña debe tener al menos 8 caracteres.';
        } else if (passwordCtrl.errors['noUppercase'] ||
          passwordCtrl.errors['noNumber'] ||
          passwordCtrl.errors['noSpecial']) {
          this.errorMessage = 'La contraseña debe incluir mayúscula, número y carácter especial.';
        }
      } else if (this.passwordForm.hasError('mismatch')) {
        this.errorMessage = 'Las contraseñas no coinciden.';
      } else {
        this.errorMessage = 'La contraseña no cumple los requisitos.';
      }
      this.cdr.detectChanges();
      return;
    }

    const nuevaPassword = this.passwordForm.get('nuevaPassword')?.value;
    this.isLoading = true;
    this.userService.changePassword(this.user.id, nuevaPassword)
      .pipe(finalize(() => {
        this.isLoading = false;
        this.cdr.detectChanges();
      }))
      .subscribe({
        next: () => {
          this.successMessage = 'Contraseña actualizada correctamente.';
          this.passwordForm.reset();
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (err: any) => {
          this.errorMessage = 'Error al cambiar la contraseña.';
          console.error(err);
        }
      });
  }

  toggleTwoFactor(): void {
    if (!this.user) {
      this.errorMessage = 'Usuario no cargado.';
      this.cdr.detectChanges();
      return;
    }
    this.twoFactorEnabled = !this.twoFactorEnabled;
    this.updateTwoFactor();
  }

  updateTwoFactor(): void {
    if (!this.user) {
      this.errorMessage = 'Usuario no cargado.';
      this.cdr.detectChanges();
      return;
    }

    const updateData: any = {
      dosFactoresHabilitado: this.twoFactorEnabled
    };
    if (this.twoFactorEnabled) {
      updateData.metodoDosFactores = this.twoFactorMethod;
    } else {
      updateData.metodoDosFactores = null;
    }

    this.isLoading = true;
    this.userService.updateUser(this.user.id, updateData)
      .pipe(finalize(() => {
        this.isLoading = false;
        this.cdr.detectChanges();
      }))
      .subscribe({
        next: (updatedUser: Usuario) => {
          this.user = updatedUser;
          this.successMessage = `Verificación en dos pasos ${this.twoFactorEnabled ? 'activada' : 'desactivada'}.`;
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (err: any) => {
          this.errorMessage = 'Error al actualizar la verificación en dos pasos.';
          console.error(err);
          this.twoFactorEnabled = !this.twoFactorEnabled;
        }
      });
  }

  deleteAccount(): void {
    if (!this.user) {
      this.errorMessage = 'Usuario no cargado.';
      this.cdr.detectChanges();
      return;
    }

    const confirmDelete = confirm('¿Estás seguro de que deseas eliminar tu cuenta? Esta acción desactivará tu cuenta y no podrás acceder hasta que un administrador la reactive.');
    if (confirmDelete) {
      this.isLoading = true;
      this.userService.deactivateUser(this.user.id)
        .pipe(finalize(() => {
          this.isLoading = false;
          this.cdr.detectChanges();
        }))
        .subscribe({
          next: () => {
            this.successMessage = 'Tu cuenta ha sido desactivada. Serás redirigido al inicio.';
            setTimeout(() => {
              this.authService.logout();
              this.router.navigate(['/']);
            }, 2000);
          },
          error: (err: any) => {
            this.errorMessage = 'Error al eliminar la cuenta.';
            console.error(err);
          }
        });
    }
  }

  onWindowScroll(): void {
    this.isNavbarScrolled = window.scrollY > 50;
  }

  toggleDarkMode(): void {
    this.isDarkMode = !this.isDarkMode;
    if (this.isDarkMode) {
      document.body.classList.add('dark-mode');
      localStorage.setItem('theme', 'dark');
    } else {
      document.body.classList.remove('dark-mode');
      localStorage.setItem('theme', 'light');
    }
  }
}
