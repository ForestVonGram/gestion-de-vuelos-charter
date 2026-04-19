import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { firstValueFrom } from 'rxjs';

import { AuthService } from '../../services/auth/auth.service';
import { VueloService, VueloCreateDTO, VueloDTO } from '../../services/vuelos/vuelo.service';
import { DisponibilidadService } from '../../services/vuelos/disponibilidad.service';
import { AccesibilidadComponent } from '../../shared/accesibilidad/accesibilidad.component';
import { ChatbotWidgetComponent } from '../../shared/chatbot-widget/chatbot-widget.component';
import { WhatsAppButtonComponent } from '../../shared/whatsapp-button/whatsapp-button.component';

@Component({
  selector: 'app-agendar-vuelo',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    AccesibilidadComponent,
    ChatbotWidgetComponent,
    WhatsAppButtonComponent
  ],
  templateUrl: './agendar-vuelo.component.html',
  styleUrls: ['./agendar-vuelo.component.css']
})
export class AgendarVueloComponent implements OnInit {
  form!: FormGroup;
  cargando = false;
  clasesServicio = ['Ejecutiva', 'Primera Clase', 'Corporativa'];
  metodosPago = ['Mercado Pago', 'Transferencia bancaria', 'Tarjeta de crédito'];

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private vueloService: VueloService,
    private disponibilidadService: DisponibilidadService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.inicializarFormulario();
  }

  private inicializarFormulario(): void {
    const currentUser = this.authService.currentUserValue;

    let nombre = '';
    let apellido = '';
    if (currentUser?.nombreCompleto) {
      const partes = currentUser.nombreCompleto.trim().split(' ');
      nombre = partes[0] || '';
      apellido = partes.slice(1).join(' ') || '';
    }

    const email = currentUser?.email || '';

    this.form = this.fb.group({
      origen: ['Bogotá (BOG)', [Validators.required, Validators.minLength(2)]],
      destino: ['Medellín (MDE)', [Validators.required, Validators.minLength(2)]],
      fechaSalida: ['', Validators.required],
      fechaLlegada: ['', Validators.required],
      numeroPasajeros: [1, [Validators.required, Validators.min(1), Validators.max(500)]],
      proposito: [''],
      claseServicio: ['Ejecutiva', Validators.required],
      observaciones: [''],

      nombre: [nombre, Validators.required],
      apellido: [apellido, Validators.required],
      tipoDocumento: ['CC', Validators.required],
      documentoIdentidad: ['', Validators.required],
      nacionalidad: ['Colombiana'],
      telefono: [''],
      email: [email, [Validators.required, Validators.email]],
      asientoPreferido: [''],
      restriccionesMedicas: [''],
      restriccionesAlimentarias: [''],
      contactoEmergencia: [''],
      telefonoEmergencia: [''],

      metodoPago: ['', Validators.required],
    });

    const ahora = new Date();
    const salida = new Date(ahora);
    salida.setDate(salida.getDate() + 1);
    salida.setHours(8, 0, 0, 0);
    const llegada = new Date(salida);
    llegada.setHours(10, 30, 0, 0);

    this.form.patchValue({
      fechaSalida: this.formatDateTimeLocal(salida),
      fechaLlegada: this.formatDateTimeLocal(llegada)
    });
  }

  private formatDateTimeLocal(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    return `${year}-${month}-${day}T${hours}:${minutes}`;
  }

  async procesarSolicitud(): Promise<void> {
    const metodoPagoControl = this.form.get('metodoPago');
    if (!metodoPagoControl?.value) {
      alert('Debes seleccionar un método de pago antes de continuar.');
      metodoPagoControl?.markAsTouched();
      return;
    }

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.cargando = true;

    try {
      const rawValues = this.form.value;
      const currentUser = this.authService.currentUserValue;
      if (!currentUser?.userId) {
        throw new Error('Usuario no autenticado');
      }

      const nvl = (val: any) => (val === null || val === undefined || val === '') ? 'N/A' : val;

      const v = {
        ...rawValues,
        proposito: nvl(rawValues.proposito),
        observaciones: nvl(rawValues.observaciones),
        telefono: nvl(rawValues.telefono),
        asientoPreferido: nvl(rawValues.asientoPreferido),
        restriccionesMedicas: nvl(rawValues.restriccionesMedicas),
        restriccionesAlimentarias: nvl(rawValues.restriccionesAlimentarias),
        contactoEmergencia: nvl(rawValues.contactoEmergencia),
        telefonoEmergencia: nvl(rawValues.telefonoEmergencia)
      };

      const vueloCreate: VueloCreateDTO = {
        usuarioId: currentUser.userId,
        origen: v.origen,
        destino: v.destino,
        fechaSalidaProgramada: new Date(v.fechaSalida).toISOString(),
        fechaLlegadaProgramada: new Date(v.fechaLlegada).toISOString(),
        numeroPasajeros: v.numeroPasajeros,
        proposito: v.proposito === 'N/A' ? undefined : v.proposito,
        observaciones: v.observaciones === 'N/A' ? undefined : v.observaciones
      };

      const vueloCreado = await firstValueFrom(this.vueloService.crearVuelo(vueloCreate));
      console.log('✅ Vuelo creado:', vueloCreado);

      // Calcular precio dinámico
      const costoEstimado = this.calcularCosto(
        v.claseServicio,
        v.fechaSalida,
        v.fechaLlegada,
        v.numeroPasajeros
      );

      // Intentar asignar aeronave (opcional)
      try {
        const aeronavesDisponibles = await firstValueFrom(
          this.disponibilidadService.consultarAeronavesDisponibles(
            vueloCreado.fechaSalidaProgramada,
            vueloCreado.fechaLlegadaProgramada,
            vueloCreado.numeroPasajeros
          )
        );
        if (aeronavesDisponibles.length > 0) {
          const aeronave = aeronavesDisponibles[0];
          await firstValueFrom(
            this.vueloService.asignarAeronave(vueloCreado.id, {
              aeronaveId: aeronave.id,
              observaciones: 'Asignación automática por disponibilidad'
            })
          );
          console.log('✅ Aeronave asignada:', aeronave.matricula);
        }
      } catch (error) {
        console.warn('⚠️ No se pudo asignar aeronave automáticamente:', error);
      }

      // Guardar datos del pasajero y precio en localStorage temporalmente
      const datosAdicionales = {
        pasajero: {
          nombreCompleto: `${v.nombre} ${v.apellido}`,
          tipoDocumento: v.tipoDocumento,
          documentoIdentidad: v.documentoIdentidad
        },
        claseServicio: v.claseServicio,
        metodoPago: v.metodoPago,
        costoEstimado: costoEstimado
      };
      localStorage.setItem(`vuelo_${vueloCreado.id}_extra`, JSON.stringify(datosAdicionales));

      // Redirigir a la boleta
      this.router.navigate(['/boleta', vueloCreado.id]);

    } catch (error) {
      console.error('❌ Error en el proceso de reserva:', error);
      alert('Ocurrió un error al procesar tu solicitud. Por favor intenta nuevamente.');
      this.cargando = false;
    }
  }

  private calcularCosto(clase: string, fechaSalida: string, fechaLlegada: string, numPasajeros: number): number {
    const salida = new Date(fechaSalida);
    const llegada = new Date(fechaLlegada);
    const diffHoras = (llegada.getTime() - salida.getTime()) / (1000 * 60 * 60);
    const dias = Math.ceil(diffHoras / 24);
    const tarifaBaseDiaria = 500000; // COP
    const multiplicadores: Record<string, number> = {
      'Ejecutiva': 1.0,
      'Primera Clase': 1.8,
      'Corporativa': 2.5,
    };
    const mult = multiplicadores[clase] ?? 1.0;
    const factorAnticipacion = this.factorAnticipacion(fechaSalida);
    return Math.round(tarifaBaseDiaria * dias * numPasajeros * mult * factorAnticipacion);
  }

  private factorAnticipacion(fechaSalida: string): number {
    const diasAnticipacion = (new Date(fechaSalida).getTime() - Date.now()) / (1000 * 60 * 60 * 24);
    if (diasAnticipacion <= 2) return 1.5;
    if (diasAnticipacion <= 7) return 1.2;
    if (diasAnticipacion >= 30) return 0.85;
    return 1.0;
  }

  campoInvalido(campo: string): boolean {
    const c = this.form.get(campo);
    return !!(c && c.invalid && c.touched);
  }
}
