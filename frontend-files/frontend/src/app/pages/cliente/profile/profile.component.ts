import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { UserService, Usuario } from '../../../services/user/user.service';
import { AuthService } from '../../../services/auth/auth.service';
import { finalize } from 'rxjs/operators';
import { AccesibilidadComponent } from '../../../shared/accesibilidad/accesibilidad.component';

@Component({
  selector: 'app-profile',
  standalone: true,
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css'],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,          // <--- AÑADIDO
    AccesibilidadComponent
  ]
})
export class ProfileComponent implements OnInit {
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

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef    // <--- AÑADIDO
  ) {}

  ngOnInit(): void {
    this.initForms();
    this.loadUserData();
    window.addEventListener('scroll', this.onWindowScroll.bind(this));
    this.isDarkMode = localStorage.getItem('theme') === 'dark';
  }

  initForms(): void {
    this.profileForm = this.fb.group({
      nombre: ['', Validators.required],
      apellido: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      telefono: ['']
    });

    this.passwordForm = this.fb.group({
      nuevaPassword: ['', [Validators.required, Validators.minLength(8)]],
      confirmarPassword: ['', Validators.required]
    }, { validators: this.passwordMatchValidator });
  }

  passwordMatchValidator(g: FormGroup): { [key: string]: boolean } | null {
    const nueva = g.get('nuevaPassword')?.value;
    const confirm = g.get('confirmarPassword')?.value;
    return nueva === confirm ? null : { mismatch: true };
  }

  loadUserData(): void {
    const currentUser = this.authService.currentUserValue;
    if (!currentUser || !currentUser.userId) {
      this.errorMessage = 'No se pudo identificar al usuario.';
      this.cdr.detectChanges();   // Forzar actualización
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

    if (this.passwordForm.invalid) {
      if (this.passwordForm.hasError('mismatch')) {
        this.errorMessage = 'Las contraseñas no coinciden.';
      } else {
        this.errorMessage = 'La contraseña debe tener al menos 8 caracteres.';
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
