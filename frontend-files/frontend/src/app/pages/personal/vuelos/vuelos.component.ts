import { Component } from '@angular/core';
import { HeaderTripulante } from "../../../shared/header-tripulante/header-tripulante.component";

@Component({
  selector: 'app-vuelos',
  imports: [HeaderTripulante],
  templateUrl: './vuelos.component.html',
  styleUrl: './vuelos.component.css',
})
export class VuelosComponent {

  vuelos!: any;

}
