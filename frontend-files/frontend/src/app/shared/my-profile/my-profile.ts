import { Component } from '@angular/core';
import { HeaderTripulante } from "../header-tripulante/header-tripulante.component";
import { ProfileService } from '../../services/auth/profile-service';
import { ProfileDto } from '../../models/profile-dto';
import { FormGroup, FormsModule, FormBuilder } from "@angular/forms";

@Component({
  selector: 'app-my-profile',
  imports: [HeaderTripulante, FormsModule],
  templateUrl: './my-profile.html',
  styleUrl: './my-profile.css',
})
export class MyProfile {

  profile: ProfileDto | null = null;
  profileForm!: FormGroup;

  constructor(private profileService: ProfileService, private formBuilder: FormBuilder) {   
    const profile = this.profileService.currentUserValue;
    if (profile) {
      this.loadProfile(profile.userId);
    }
  }

  private loadProfile(id: number): void {

    this.profileService.getProfile(id).subscribe({
      next: (profile) => {
        this.profile = profile;
      },
      error: (error) => {
        console.error('Error al cargar el perfil:', error);
      }
    });
  }
}
