import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule} from '@angular/forms';
import { EstadoAeronave } from '../flota-aerea-admin/flota-aerea-admin.component';
import { AdminSidebarComponent } from "../../../shared/admin-sidebar/admin-sidebar.component";

/**
 * Componente para la creación de nuevas aeronaves.
 * Proporciona un formulario con validaciones para registrar una aeronave en el sistema.
 */
@Component({
  selector: 'app-create-plane',
  imports: [ReactiveFormsModule, AdminSidebarComponent],
  templateUrl: './create-plane.html',
  styleUrl: './create-plane.css',
})
export class CreatePlane implements OnInit {

  // Formulario reactivo para capturar datos de la aeronave
  avionForm!: FormGroup;

  // Indica si el formulario ha sido enviado (para mostrar validaciones)
  submitted = false;

  // Lista de estados posibles para la aeronave (para el select)
  estados = Object.values(EstadoAeronave);

  /**
   * Constructor del componente
   * @param fb FormBuilder para crear el formulario reactivo
   */
  constructor(private fb: FormBuilder) {}

  /**
   * Inicialización del componente.
   * Configura el formulario con sus campos y validaciones.
   */
  ngOnInit(): void {

    this.avionForm = this.fb.group({

      matricula: ['', Validators.required], // Matrícula de la aeronave (obligatoria)
      modelo: ['', Validators.required], // Modelo de la aeronave (obligatorio)

      fabricante: [''], // Fabricante (opcional)
      capacidadPasajeros: [null, Validators.required], // Capacidad de pasajeros (obligatoria)
      capacidadTripulacion: [null, Validators.required], // Capacidad de tripulación (obligatoria)
      autonomiaKm: [null, Validators.required], // Autonomía en km (obligatoria)
      velocidadCruceroKmh: [null, Validators.required], // Velocidad de crucero (obligatoria)
      fechaFabricacion: ['', Validators.required], // Fecha de fabricación (obligatoria)
      fechaUltimaRevision: ['', Validators.required], // Fecha última revisión (obligatoria)
      estado: ['ACTIVO'], // Estado inicial (ACTIVO por defecto)
      especificacionesTecnicas: ['', Validators.required] // Especificaciones técnicas (obligatorias)
    });

  }

  /**
   * Procesa el envío del formulario para crear una nueva aeronave.
   * Valida los datos y si son correctos, envía la información al backend.
   */
  crearAvion(){

    this.submitted = true; // Marcar como enviado para mostrar validaciones

    if(this.avionForm.invalid){
      return; // Si el formulario es inválido, no continuar
    }

    const avion = this.avionForm.value; // Obtener valores del formulario

    console.log(avion); // TODO: Reemplazar con llamada al servicio

  }

  /**
   * Previene la entrada de caracteres no permitidos en campos numéricos.
   * Bloquea los caracteres '-' y 'e' para evitar números negativos o notación científica.
   * @param event evento de teclado
   */
  blockNegative(event: KeyboardEvent) {

    if (event.key === '-' || event.key === 'e') {
      event.preventDefault(); // Cancela la entrada del carácter
    }

  }

  /**
   * Sanitiza un campo numérico para asegurar que no tenga valores negativos.
   * Si el valor es menor que 0, lo establece en 0.
   * @param controlName nombre del control a sanitizar
   */
  sanitizeNumber(controlName: string) {

    const control = this.avionForm.get(controlName);

    if (!control) return; // Si el control no existe, salir

    const value = control.value;

    if (value < 0) {
      control.setValue(0); // Ajustar a 0 si es negativo
    }
  }

}
