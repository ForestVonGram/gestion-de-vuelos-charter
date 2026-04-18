import { Component } from '@angular/core';
import { AdminSidebarComponent } from '../../../shared/admin-sidebar/admin-sidebar.component';
import { AccesibilidadComponent } from '../../../shared/accesibilidad/accesibilidad.component';
import { AuthService } from '../../../services/auth/auth.service';
import { UserService, Usuario } from '../../../services/user/user.service';
import { CommonModule } from '@angular/common';
import { ChangeDetectorRef } from '@angular/core';
import Swal from 'sweetalert2';
import {ChatbotWidgetComponent} from '../../../shared/chatbot-widget/chatbot-widget.component';
import {WhatsAppButtonComponent} from '../../../shared/whatsapp-button/whatsapp-button.component';
@Component({
  selector: 'app-usuarios',
  imports: [AdminSidebarComponent, AccesibilidadComponent, CommonModule, ChatbotWidgetComponent, WhatsAppButtonComponent],
  templateUrl: './usuarios.html',
  styleUrl: './usuarios.css',
})
export class Usuarios {

  currentUser!:any;
  usuarios: any[] = [];

  constructor(private authService: AuthService, private userService: UserService,
     private cdr: ChangeDetectorRef) {
    this.currentUser = this.authService.currentUserValue;
    this.getUsuarios();
  }

  getUsuarios(): void {
    this.userService.getAllUsers().subscribe({
      next: (usuarios) => {
        this.usuarios = usuarios;
        this.cdr.detectChanges(); // Asegura que la vista se actualice con los nuevos datos
      },
      error: (error) => {
        console.error('Error al obtener usuarios:', error);
      }
    });
  }

  getRolClase(rol: string): string {
  switch (rol) {
    case 'ADMINISTRADOR': return 'rol-admin';
    case 'OPERADOR_LOGISTICA': return 'rol-operador';
    case 'AYUDANTE_MANTENIMIENTO': return 'rol-mantenimiento';
    case 'TRIPULACION': return 'rol-tripulacion';
    case 'USUARIO': return 'rol-usuario';
    default: return 'rol-usuario';
  }
}

  onDelete(userId: number): void {
    Swal.fire({
      title: '¿Estás seguro?',
      text: 'Esta acción no se puede deshacer.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.userService.deactivateUser(userId).subscribe({
          next: () => {
            Swal.fire('¡Eliminado!', 'El usuario ha sido eliminado.', 'success');
            this.getUsuarios(); // Refresca la lista de usuarios después de eliminar
          }
        });
      }
    });
  }



}
