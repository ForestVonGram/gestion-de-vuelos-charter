import { Component, OnInit } from '@angular/core';
import { HeaderTripulante } from "../../../shared/header-tripulante/header-tripulante.component";
import {AccesibilidadComponent} from '../../../shared/accesibilidad/accesibilidad.component';

@Component({
  selector: 'app-tripulacion',
  imports: [HeaderTripulante, AccesibilidadComponent],
  templateUrl: './tripulacion.component.html',
  styleUrl: './tripulacion.component.css',
})
export class TripulacionComponent implements OnInit {
  personal!: any;

  ngOnInit(): void {}

}
