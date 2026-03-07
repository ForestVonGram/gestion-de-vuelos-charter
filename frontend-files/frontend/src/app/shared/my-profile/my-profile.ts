import { Component, OnInit } from '@angular/core';
import { HeaderTripulante } from "../header-tripulante/header-tripulante.component";
import { ProfileService } from '../../services/auth/profile-service';
import { ProfileDto } from '../../models/users/profile-dto';
import { ChangeDetectorRef } from '@angular/core';
import { FormGroup, FormsModule, FormBuilder, ReactiveFormsModule } from "@angular/forms";

@Component({
  selector: 'app-my-profile',
  imports: [HeaderTripulante, FormsModule, ReactiveFormsModule],
  templateUrl: './my-profile.html',
  styleUrl: './my-profile.css',
})
export class MyProfile implements OnInit {

  profile: ProfileDto | null = null;
  profileForm!: FormGroup;

  constructor(
    private profileService: ProfileService,
    private formBuilder: FormBuilder,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const user = this.profileService.currentUserValue;

    if (user) {
      this.loadProfile(user.userId);
    }
  }

  private loadProfile(id: number): void {

  this.profileService.getProfile(id).subscribe({
    next: (profile) => {
      this.profile = profile;
      this.cdr.detectChanges();
    },
    error: (error) => {
      console.error('Error al cargar el perfil:', error);
    }
  });

  }

}