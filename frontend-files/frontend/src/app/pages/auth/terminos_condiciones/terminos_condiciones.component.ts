import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-terminos-condiciones',
  templateUrl: './terminos_condiciones.component.html',
  styleUrls: ['./terminos_condiciones.component.css'],
  standalone: true,
  imports: [CommonModule]
})
export class TerminosCondicionesComponent {

  constructor(private router: Router) {}

  regresar(): void {
    // Navega de vuelta a la página de registro
    this.router.navigate(['/auth/register']);
  }
}
