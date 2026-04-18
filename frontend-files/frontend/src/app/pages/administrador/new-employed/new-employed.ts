import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CargoPersonal } from '../../../models/personal/cargo';
import { AdminSidebarComponent } from '../../../shared/admin-sidebar/admin-sidebar.component';
import { AuthService } from '../../../services/auth/auth.service';
import { PersonalService } from '../../../services/personal/personal-service';
import Swal from 'sweetalert2';
import {AccesibilidadComponent} from '../../../shared/accesibilidad/accesibilidad.component';
import {ChatbotWidgetComponent} from '../../../shared/chatbot-widget/chatbot-widget.component';
import {WhatsAppButtonComponent} from '../../../shared/whatsapp-button/whatsapp-button.component';

/**
 * Componente para la creación de nuevo personal.
 * Proporciona un formulario con validaciones para registrar empleados en el sistema.
 */
@Component({
  selector: 'app-new-employed',
  imports: [ReactiveFormsModule, AdminSidebarComponent, AccesibilidadComponent, ChatbotWidgetComponent, WhatsAppButtonComponent],
  templateUrl: './new-employed.html',
  styleUrl: './new-employed.css',
})
export class NewEmployed {

  // Formulario reactivo para capturar datos del personal
  personalForm!: FormGroup;

  // Lista de cargos disponibles (del enum CargoPersonal)
  cargos = Object.values(CargoPersonal);

  // Usuario actualmente autenticado
  currentUser: any = null;

  // Indica si el formulario ha sido enviado (para mostrar validaciones)
  submitted = false;

  /**
   * Constructor del componente
   * @param fb FormBuilder para crear el formulario reactivo
   * @param authService servicio de autenticación
   * @param personalService servicio para operaciones con personal
   */
  constructor(private fb: FormBuilder, private authService: AuthService, private personalService: PersonalService) {
    this.currentUser = this.authService.currentUserValue;
  }

  /**
   * Inicialización del componente.
   * Configura el formulario con sus campos y validaciones.
   */
  ngOnInit(): void {

    this.personalForm = this.fb.group({

      usuarioId: [null, Validators.required], // ID del usuario asociado (obligatorio)

      numeroEmpleado: ['', Validators.required], // Número único de empleado (obligatorio)

      cargo: [null, Validators.required], // Cargo del empleado (obligatorio)

      areaEspecializacion: [''], // Área de especialización (opcional)

      certificaciones: [''], // Certificaciones del empleado (opcional)

      fechaContratacion: [''], // Fecha de contratación (opcional)

      turno: [''], // Turno asignado (opcional)

      observaciones: [''] // Observaciones adicionales (opcional)

    });

  }

  /**
   * Guarda los datos del nuevo personal en el backend.
   * Valida el formulario y muestra mensajes de éxito o error.
   */
  guardarPersonal() {

    if (this.personalForm.invalid) {
      this.personalForm.markAllAsTouched(); // Marcar todos los campos como tocados para mostrar errores
      return;
    }

    const datos = this.personalForm.value; // Obtener valores del formulario
    this.personalService.crearPersonal(datos).subscribe({
      next: (response) => {
        console.log("Personal creado exitosamente:", response);
        this.personalForm.reset(); // Limpiar formulario
        this.submitted = false; // Reiniciar estado de envío
        Swal.fire({
          icon: 'success',
          title: '¡Éxito!',
          text: 'El personal ha sido creado exitosamente.',
          confirmButtonText: 'Aceptar'
        });
      },
      error: (error) => {
        console.error("Error al crear personal:", error);
        Swal.fire({
          icon: 'error',
          title: '¡Error!',
          text: 'Ocurrió un error al crear el personal. Por favor, inténtelo de nuevo.',
          confirmButtonText: 'Aceptar'
        });
      }
    });

  }

  /**
   * Verifica si un campo del formulario es inválido y ha sido tocado.
   * @param campo nombre del campo a verificar
   * @returns true si el campo es inválido y ha sido tocado
   */
  campoInvalido(campo: string): boolean {
    const control = this.personalForm.get(campo);
    return !!(control && control.invalid && control.touched);
  }

  /**
   * Previene la entrada de caracteres no permitidos en campos numéricos.
   * Bloquea los caracteres '-', 'e', '+', '.'.
   * @param event evento de teclado
   */
  blockNegative(event: KeyboardEvent) {
    if (event.key === '-' || event.key === 'e' || event.key === '+' || event.key === '.') {
      event.preventDefault(); // Cancela la entrada del carácter
    }
  }

  /**
   * Sanitiza un campo numérico para asegurar que no tenga valores negativos.
   * Si el valor es menor que 0, lo establece en 0.
   * @param controlName nombre del control a sanitizar
   */
  sanitizeNumber(controlName: string) {
    const control = this.personalForm.get(controlName);
    if (!control) return;

    let value = Number(control.value);
    if (isNaN(value) || value < 0) {
      control.setValue(0); // Ajustar a 0 si no es número o es negativo
    }
  }

}
