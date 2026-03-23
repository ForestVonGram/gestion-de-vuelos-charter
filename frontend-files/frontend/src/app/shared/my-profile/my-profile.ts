import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { HeaderTripulante } from "../header-tripulante/header-tripulante.component";
import { ProfileService } from '../../services/auth/profile-service';
import { ProfileDto } from '../../models/users/profile-dto';
import { FormGroup, FormsModule, FormBuilder, ReactiveFormsModule } from "@angular/forms";

@Component({
  selector: 'app-my-profile',
  standalone: true, // Arquitectura modular
  imports: [HeaderTripulante, FormsModule, ReactiveFormsModule],
  templateUrl: './my-profile.html',
  styleUrl: './my-profile.css',
})
export class MyProfile implements OnInit {

  // --- Estado del Perfil ---
  profile: ProfileDto | null = null; // Almacena los datos detallados del tripulante
  profileForm!: FormGroup;           // Preparado para futura edición de datos

  constructor(
    private profileService: ProfileService, // Servicio de datos de perfil
    private formBuilder: FormBuilder,       // Utilidad para construir formularios
    private cdr: ChangeDetectorRef          // Fuerza la detección de cambios en la UI
  ) {}

  /**
   * Ciclo de vida: Al iniciar, recupera el ID del usuario desde la sesión actual
   * e invoca la carga de datos del perfil.
   */
  ngOnInit(): void {
    const user = this.profileService.currentUserValue;

    if (user) {
      this.loadProfile(user.userId);
    }
  }

  /**
   * Lógica de obtención de datos: Consulta al backend mediante el servicio
   * y actualiza la vista tras recibir la respuesta.
   */
  private loadProfile(id: number): void {
    this.profileService.getProfile(id).subscribe({
      next: (profile) => {
        this.profile = profile;
        // Notifica manualmente a Angular que los datos han llegado para renderizar la vista
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error al cargar el perfil:', error);
      }
    });
  }
}
