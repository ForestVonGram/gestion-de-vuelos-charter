import { Component, input } from '@angular/core';
import { HeaderTripulante } from "../../../shared/header-tripulante";

@Component({
  selector: 'app-certificados',
  imports: [HeaderTripulante],
  templateUrl: './certificados.html',
  styleUrl: './certificados.css',
})
export class Certificados {

  certificados = input<any[]>([]);

  onFileSelected(event: any) {
    const file: File = event.target.files[0];
    if (file) {
      console.log("Selected file:", file.name);
    }
  }
}
