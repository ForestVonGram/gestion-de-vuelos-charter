import { Component } from '@angular/core';
import { RouterLink } from "@angular/router";
import { AuthService } from '../../services/auth/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-header-tripulante',
  imports: [RouterLink],
  templateUrl: './header-tripulante.component.html',
  styleUrl: './header-tripulante.component.css',
})
export class HeaderTripulante {

  userName!: string;
  userEmail!: string;
  isDropdownOpen: boolean = false;

  constructor(private authService: AuthService, private router: Router) {
    const currentUser = this.authService.currentUserValue;
    if (currentUser) {
      this.userName = currentUser.nombreCompleto;
      this.userEmail = currentUser.email;
    }
  }

  toggleDropdown() {
    console.log("Toggle dropdown")
    this.isDropdownOpen = !this.isDropdownOpen;
  }

  logOut(): void {
    this.authService.logout(); // Limpia token y localstorage
    this.router.navigate(['/auth/login']); // Redirige al login
  }

}
