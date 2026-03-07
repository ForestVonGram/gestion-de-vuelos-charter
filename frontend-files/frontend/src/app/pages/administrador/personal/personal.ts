import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { OnInit, ChangeDetectorRef } from '@angular/core';
import {AdminSidebarComponent} from '../../../shared/admin-sidebar/admin-sidebar.component';
import { AuthService } from '../../../services/auth/auth.service';
import { PersonalService } from '../../../services/personal/personal-service';
import { CargoPersonal } from '../../../models/personal/cargo';
import { EstadoPersonal } from '../../../models/personal/estado-personal';

@Component({
  selector: 'app-personal',
  imports: [FormsModule, AdminSidebarComponent],
  templateUrl: './personal.html',
  styleUrl: './personal.css',
})
export class Personal implements OnInit {

  currentUser : any = null;
  cargos: any[] = Object.values(CargoPersonal);
  estados: any[] = Object.values(EstadoPersonal);
  filtroNombre: string = '';
  filtroEstado: string = '';
  filtroCargo: string = '';
  personalFiltrado: any[] = [];

  constructor(private authService: AuthService, private personalService: PersonalService, private cdr: ChangeDetectorRef) {
    this.currentUser = this.authService.currentUserValue;
      console.log('cargos:', this.cargos);
      console.log('estados:', this.estados);
      console.log(this.currentUser);
  }

  ngOnInit(): void {
    this.personalService.obtenerPersonal().subscribe({
      next: (response) => {
        this.personalFiltrado = response;
        console.log("Personal obtenido exitosamente:", response);
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error al obtener el personal:', error);
      }
    });
  }

  formatear(valor: string): string {
  return valor.replace(/_/g, ' ');
}

  filtrar(): void {
  this.personalService.filtroPersonal(this.filtroNombre, this.filtroEstado, this.filtroCargo).subscribe({
    next: (response) => {
      this.personalFiltrado = [...response];
      this.cdr.detectChanges();
      console.log("Personal filtrado exitosamente:", response);
    },
    error: (error) => {
      console.error('Error al filtrar:', error);
    }
  });
}
  

  
  getInitials(name: string): string {
    const names = name.split(' ');
    const initials = names.map(n => n.charAt(0).toUpperCase()).join('');
    return initials;
  }


  formatDate(dateString: string): string {
    const options: Intl.DateTimeFormatOptions = { year: 'numeric', month: 'long', day: 'numeric' };
    return new Date(dateString).toLocaleDateString(undefined, options);
  }

}
