import { ChangeDetectorRef, Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {AdminSidebarComponent} from '../../../shared/admin-sidebar/admin-sidebar.component';
import { PersonalService } from '../../../services/personal/personal-service';
import { CargoPersonal } from '../../../models/personal/cargo';
import { EstadoPersonal } from '../../../models/personal/estado-personal';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../services/auth/auth.service';
import { OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-detalles-personal',
  imports: [AdminSidebarComponent, CommonModule, FormsModule],
  templateUrl: './detalles-personal.html',
  styleUrl: './detalles-personal.css',
})
export class DetallesPersonal implements OnInit {

  persona:any
  cargos: any[] = Object.values(CargoPersonal);
  estados: any[] = Object.values(EstadoPersonal);
  currentUser: any


  constructor(private personalService: PersonalService, private authService: AuthService, 
    private activatedRoute: ActivatedRoute, private cdr: ChangeDetectorRef) {
    this.currentUser = this.authService.currentUserValue;
  }

  ngOnInit(): void {
    const id = this.activatedRoute.snapshot.paramMap.get('id');  
    if (id) {
      this.personalService.obtenerPersonalPorId(+id).subscribe({
        next: (response) => {
          this.persona = response;
          this.cdr.detectChanges();
        },
        error: (error) => {
          Swal.fire('Error', 'No se pudo cargar la información del personal.', 'error');
        }
      });
    }
  }

    formatear(valor: string): string {
  return valor.replace(/_/g, ' ');
  }

  volver(): void {
    window.history.back();
  }

  guardar(): void {
    const id = this.activatedRoute.snapshot.paramMap.get('id');
    if (id) {
      this.personalService.actualizarPersonal(+id, this.persona).subscribe({
        next: (response) => {
          Swal.fire('Éxito', 'Información del personal actualizada correctamente.', 'success');
        },
        error: (error) => {
          Swal.fire('Error', 'No se pudo actualizar la información del personal.', 'error');
        }
      });
    }
  }


}
