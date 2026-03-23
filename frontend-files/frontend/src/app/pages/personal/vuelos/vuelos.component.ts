import { Component } from '@angular/core';
import { HeaderTripulante } from "../../../shared/header-tripulante/header-tripulante.component";
import {AccesibilidadComponent} from '../../../shared/accesibilidad/accesibilidad.component';

@Component({
  selector: 'app-vuelos',
  imports: [HeaderTripulante, AccesibilidadComponent],
  templateUrl: './vuelos.component.html',
  styleUrl: './vuelos.component.css',
})
export class VuelosComponent {

  vuelos!: any;

}
