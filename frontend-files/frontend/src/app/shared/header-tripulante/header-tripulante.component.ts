import { Component } from '@angular/core';
import { RouterLink } from "@angular/router";

@Component({
  selector: 'app-header-tripulante',
  imports: [RouterLink],
  templateUrl: './header-tripulante.component.html',
  styleUrl: './header-tripulante.component.css',
})
export class HeaderTripulante {

  userName: string = "Elkin"
  userEmail: string = "elkin@astranimbus.com"
  isDropdownOpen: boolean = false;

  toggleDropdown() {
    console.log("Toggle dropdown")
    this.isDropdownOpen = !this.isDropdownOpen;
  }

  logOut() {
    console.log("Log out")
  }

}
