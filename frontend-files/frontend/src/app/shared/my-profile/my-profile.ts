import { Component } from '@angular/core';
import { HeaderTripulante } from "../header-tripulante/header-tripulante.component";

@Component({
  selector: 'app-my-profile',
  imports: [HeaderTripulante],
  templateUrl: './my-profile.html',
  styleUrl: './my-profile.css',
})
export class MyProfile {

  user!: any


  constructor() {    this.user = {
      nombre: "Elkin",
      apellido: "García",
      email: "elkin.garcia@example.com",
      telefono: "123-456-7890",
      activo: true,
      fechaRegistro: "2024-01-01"
    }
  }

}
