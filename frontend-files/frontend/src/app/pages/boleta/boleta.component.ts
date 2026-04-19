import { Component, OnInit, ViewChild, ElementRef, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { firstValueFrom } from 'rxjs';

import { AuthService} from '../../services/auth/auth.service';
import { VueloService, VueloCreateDTO, VueloDTO} from '../../services/vuelos/vuelo.service';
import { DisponibilidadService} from '../../services/vuelos/disponibilidad.service';
import {AccesibilidadComponent} from '../../shared/accesibilidad/accesibilidad.component';
import {ChatbotWidgetComponent} from '../../shared/chatbot-widget/chatbot-widget.component';
import {WhatsAppButtonComponent} from '../../shared/whatsapp-button/whatsapp-button.component';

export type EstadoVuelo = 'SOLICITADO' | 'CONFIRMADO' | 'EN_CURSO' | 'COMPLETADO' | 'CANCELADO' | 'DEMORADO';

export interface BoletaData {
  vuelo: VueloDTO;
  pasajero: any;
  codigoReserva: string;
  numeroAsiento: string;
  puertaEmbarque: string;
  claseServicio: string;
  codigoQR: string;
  fechaEmision: string;
  montoTotal: number;
  metodoPago: string;
  transaccionId: string;
}

@Component({
  selector: 'app-boleta',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, AccesibilidadComponent, ChatbotWidgetComponent, WhatsAppButtonComponent],
  templateUrl: './boleta.component.html',
  styleUrls: ['./boleta.component.css'],
})
export class BoletaComponent implements OnInit {
  @ViewChild('boletaRef') boletaRef!: ElementRef;

  vistaActual: 'formulario' | 'recibo' = 'formulario';
  boletaData: BoletaData | null = null;
  cargando = false;
  form!: FormGroup;
  clasesServicio = ['Ejecutiva', 'Primera Clase', 'Corporativa'];
  metodosPago = ['Mercado Pago', 'Transferencia bancaria', 'Tarjeta de crédito'];

  // Modo consulta (ver vuelo existente)
  modoConsulta: boolean = false;
  vueloId: number | null = null;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private vueloService: VueloService,
    private disponibilidadService: DisponibilidadService,
    private cdr: ChangeDetectorRef,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Detectar si estamos en modo consulta (ruta /vuelo/:id)
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.modoConsulta = true;
        this.vueloId = +params['id'];
        this.cargarVueloExistente();
      } else {
        this.inicializarFormulario();
      }
    });
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

  private cargarVueloExistente(): void {
    if (!this.vueloId) return;
    this.cargando = true;
    this.vueloService.getVueloById(this.vueloId).subscribe({
      next: (vuelo) => {
        // Construir boletaData con los datos disponibles
        this.boletaData = {
          vuelo: vuelo,
          pasajero: {
            nombreCompleto: vuelo.usuarioNombre || 'Pasajero',
            tipoDocumento: 'CC',
            documentoIdentidad: 'N/A'
          },
          codigoReserva: this.generarCodigoReserva(),
          numeroAsiento: this.asignarAsientoAuto(),
          puertaEmbarque: this.asignarPuerta(),
          claseServicio: 'Ejecutiva',
          codigoQR: this.generarCodigoQR(vuelo.id),
          fechaEmision: vuelo.fechaSolicitud,
          montoTotal: vuelo.costoEstimado || 0,
          metodoPago: 'No especificado',
          transaccionId: this.generarTransaccionId()
        };
        this.vistaActual = 'recibo';
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error cargando vuelo:', err);
        alert('No se pudo cargar el vuelo solicitado.');
        this.cargando = false;
        this.router.navigate(['/dashboard']);
      }
    });
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

      const pasajeroSimulado = {
        id: 0,
        vueloId: vueloCreado.id,
        nombre: v.nombre,
        apellido: v.apellido,
        nombreCompleto: `${v.nombre} ${v.apellido}`,
        documentoIdentidad: v.documentoIdentidad,
        tipoDocumento: v.tipoDocumento,
        nacionalidad: v.nacionalidad,
        telefono: v.telefono,
        email: v.email,
        contactoEmergencia: v.contactoEmergencia,
        telefonoEmergencia: v.telefonoEmergencia,
        restriccionesMedicas: v.restriccionesMedicas,
        restriccionesAlimentarias: v.restriccionesAlimentarias,
        asientoPreferido: v.asientoPreferido,
        observaciones: v.observaciones
      };

      let aeronaveAsignada = false;
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
          vueloCreado.aeronaveId = aeronave.id;
          vueloCreado.aeronaveMatricula = aeronave.matricula;
          aeronaveAsignada = true;
          console.log('✅ Aeronave asignada:', aeronave.matricula);
        }
      } catch (error) {
        console.warn('⚠️ No se pudo asignar aeronave automáticamente:', error);
      }

      if (!aeronaveAsignada) {
        vueloCreado.aeronaveMatricula = 'Por asignar';
      }

      const costoEstimado = this.calcularCosto(
        v.claseServicio,
        v.fechaSalida,
        v.fechaLlegada,
        v.numeroPasajeros
      );

      this.boletaData = {
        vuelo: vueloCreado,
        pasajero: pasajeroSimulado,
        codigoReserva: this.generarCodigoReserva(),
        numeroAsiento: v.asientoPreferido !== 'N/A' ? v.asientoPreferido : this.asignarAsientoAuto(),
        puertaEmbarque: this.asignarPuerta(),
        claseServicio: v.claseServicio,
        codigoQR: this.generarCodigoQR(vueloCreado.id),
        fechaEmision: new Date().toISOString(),
        montoTotal: costoEstimado,
        metodoPago: v.metodoPago,
        transaccionId: this.generarTransaccionId()
      };

      this.cargando = false;
      this.vistaActual = 'recibo';
      this.cdr.detectChanges();

      setTimeout(() => {
        if (this.boletaRef && this.boletaData) {
          this.descargarPDF();
        } else {
          console.warn('No se pudo generar PDF automáticamente. Usa el botón "Descargar PDF".');
        }
      }, 600);

    } catch (error) {
      console.error('❌ Error en el proceso de reserva:', error);
      alert('Ocurrió un error al procesar tu solicitud. Por favor intenta nuevamente.');
      this.cargando = false;
    }
  }

  // ── Helpers de generación ────────────────────
  private generarCodigoReserva(): string {
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
    return Array.from({ length: 6 }, () => chars[Math.floor(Math.random() * chars.length)]).join('');
  }

  private generarCodigoQR(vueloId: number): string {
    return `ANV-${vueloId}-${Date.now()}`;
  }

  private generarTransaccionId(): string {
    return `TXN-${Date.now()}-${Math.floor(Math.random() * 9999)}`;
  }

  private asignarAsientoAuto(): string {
    const fila = Math.floor(Math.random() * 20) + 1;
    const col = ['A', 'B', 'C', 'D'][Math.floor(Math.random() * 4)];
    return `${fila}${col}`;
  }

  private asignarPuerta(): string {
    const numero = Math.floor(Math.random() * 30) + 1;
    return `G${numero}`;
  }

  private calcularCosto(clase: string, fechaSalida: string, fechaLlegada: string, numPasajeros: number): number {
    const salida = new Date(fechaSalida);
    const llegada = new Date(fechaLlegada);
    const diffHoras = (llegada.getTime() - salida.getTime()) / (1000 * 60 * 60);
    const dias = Math.ceil(diffHoras / 24);
    const tarifaBaseDiaria = 500000;
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

  // ── Formatters ───────────────────────────────
  formatFecha(iso: string): string {
    if (!iso) return '';
    const d = new Date(iso);
    return d.toLocaleDateString('es-CO', { day: '2-digit', month: 'short', year: 'numeric' });
  }

  formatHora(iso: string): string {
    if (!iso) return '';
    const d = new Date(iso);
    return d.toLocaleTimeString('es-CO', { hour: '2-digit', minute: '2-digit', hour12: false });
  }

  formatMoneda(valor: number): string {
    return new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 }).format(valor);
  }

  calcularDuracion(salida: string, llegada: string): string {
    const diff = new Date(llegada).getTime() - new Date(salida).getTime();
    const horas = Math.floor(diff / (1000 * 60 * 60));
    const minutos = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
    return `${horas}h ${minutos}m`;
  }

  extraerCiudad(lugar: string): string {
    return lugar.split('(')[0].trim();
  }

  extraerCodigo(lugar: string): string {
    const match = lugar.match(/\(([^)]+)\)/);
    return match ? match[1] : '';
  }

  campoInvalido(campo: string): boolean {
    const c = this.form?.get(campo);
    return !!(c && c.invalid && c.touched);
  }

  volverAlFormulario(): void {
    this.vistaActual = 'formulario';
    this.boletaData = null;
    this.inicializarFormulario();
  }

  // ── PDF Download con estilos COMPLETOS ─────────────────────
  descargarPDF(): void {
    if (!this.boletaRef || !this.boletaData) {
      console.warn('No se puede generar PDF: faltan datos');
      return;
    }

    try {
      const ticketHTML = this.boletaRef.nativeElement.outerHTML;
      const codigo = this.boletaData.codigoReserva;
      const pasajero = this.boletaData.pasajero.nombreCompleto;

      const printWindow = window.open('', '_blank', 'width=1100,height=750');
      if (!printWindow) {
        alert('El navegador bloqueó la ventana emergente. Permite ventanas emergentes para este sitio.');
        return;
      }

      printWindow.document.write(this.buildPrintDocument(ticketHTML, codigo, pasajero));
      printWindow.document.close();

      printWindow.onload = () => {
        printWindow.focus();
        printWindow.print();
        printWindow.onafterprint = () => printWindow.close();
      };
    } catch (error) {
      console.error('Error al generar PDF:', error);
      alert('No se pudo generar el PDF. Puedes intentar descargarlo manualmente.');
    }
  }

  private buildPrintDocument(ticketHTML: string, codigo: string, pasajero: string): string {
    return `
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8" />
  <title>Boleta AstraNimbus — ${codigo}</title>

  <!-- Fuentes -->
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Archivo+Black&family=Jost:wght@300;400;600&family=Libre+Franklin:wght@400;600;700&display=swap" rel="stylesheet" />

  <style>
    /* ── RESET Y VARIABLES ────────────────────────── */
    *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

    :root {
      --an-dark:      #1a1a4e;
      --an-mid:       #5c5c99;
      --an-light:     #f0f0fa;
      --an-light-mid: #d8d8f0;
      --an-white:     #ffffff;
      --an-success:   #2d9e6b;
      --an-subtext:   #6b6b9a;
      --an-text:      #2c2c54;
      --radius-sm:    6px;
      --radius-md:    12px;
      --radius-lg:    20px;
      --radius-xl:    28px;
    }

    @page {
      size: A4 landscape;
      margin: 12mm 16mm;
    }

    body {
      font-family: 'Libre Franklin', sans-serif;
      background: #ffffff;
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 24px 0;
      -webkit-print-color-adjust: exact;
      print-color-adjust: exact;
    }

    /* ── ENCABEZADO DEL DOCUMENTO ─────────────────── */
    .print-doc-header {
      width: 100%;
      max-width: 880px;
      display: flex;
      justify-content: space-between;
      align-items: flex-end;
      margin-bottom: 16px;
      padding-bottom: 12px;
      border-bottom: 1px solid var(--an-light-mid);
    }

    .print-doc-title {
      font-family: 'Archivo Black', sans-serif;
      font-size: 1rem;
      color: var(--an-dark);
      letter-spacing: 0.04em;
    }

    .print-doc-meta {
      font-family: 'Libre Franklin', sans-serif;
      font-size: 0.72rem;
      color: var(--an-subtext);
      text-align: right;
      line-height: 1.6;
    }

    /* ── CONTENEDOR DEL TICKET ────────────────────── */
    .ticket-outer { width: 100%; max-width: 880px; }

    /* ── TICKET CARD ──────────────────────────────── */
    .ticket {
      background: var(--an-white);
      border-radius: var(--radius-xl);
      overflow: hidden;
      border: 1.5px solid var(--an-light-mid);
    }

    /* CABECERA */
    .ticket-header {
      background: linear-gradient(135deg, var(--an-dark) 0%, var(--an-mid) 100%);
      padding: 20px 32px;
      display: flex;
      align-items: center;
      justify-content: space-between;
    }

    .ticket-airline { display: flex; align-items: center; gap: 12px; }

    .ticket-logo {
      width: 44px; height: 44px;
      object-fit: contain;
      filter: brightness(0) invert(1);
    }

    .ticket-airline-name {
      font-family: 'Jost', sans-serif;
      font-size: 1.3rem;
      font-style: italic;
      color: #fff;
      display: block;
    }

    .ticket-airline-sub {
      font-family: 'Libre Franklin', sans-serif;
      font-size: 0.65rem;
      color: rgba(255,255,255,.6);
      text-transform: uppercase;
      letter-spacing: .08em;
      display: block;
    }

    .ticket-meta-right { text-align: right; }

    .ticket-class-badge {
      background: rgba(255,255,255,.15);
      color: #fff;
      border: 1px solid rgba(255,255,255,.3);
      border-radius: 20px;
      padding: 2px 12px;
      font-size: 0.7rem;
      font-weight: 700;
      text-transform: uppercase;
      letter-spacing: .07em;
      display: inline-block;
      margin-bottom: 4px;
    }

    .ticket-flight-num {
      font-size: 0.8rem;
      color: rgba(255,255,255,.8);
    }

    .ticket-flight-num strong { color: #fff; font-size: 1rem; }

    .ticket-fecha-emision {
      font-size: 0.66rem;
      color: rgba(255,255,255,.5);
    }

    /* RUTA */
    .ticket-route {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 28px 32px 22px;
      gap: 12px;
    }

    .route-city { display: flex; flex-direction: column; align-items: center; text-align: center; gap: 2px; }

    .route-code {
      font-family: 'Archivo Black', sans-serif;
      font-size: 3rem;
      color: var(--an-dark);
      line-height: 1;
    }

    .route-name { font-size: 0.82rem; color: var(--an-subtext); font-weight: 600; }
    .route-time { font-family: 'Jost', sans-serif; font-size: 1.6rem; font-weight: 300; color: var(--an-dark); margin-top: 4px; }
    .route-date { font-size: 0.72rem; color: var(--an-subtext); }

    .route-middle { flex: 1; display: flex; flex-direction: column; align-items: center; gap: 6px; }

    .route-duration { display: flex; flex-direction: column; align-items: center; }
    .duration-label { font-size: 0.62rem; color: var(--an-subtext); text-transform: uppercase; letter-spacing: .06em; }
    .duration-val { font-family: 'Jost', sans-serif; font-size: 0.9rem; font-weight: 600; color: var(--an-mid); }

    .route-line { display: flex; align-items: center; width: 100%; gap: 4px; }
    .route-dot { width: 7px; height: 7px; border-radius: 50%; background: var(--an-mid); flex-shrink: 0; }
    .route-track { flex: 1; height: 1.5px; background: linear-gradient(90deg, var(--an-mid), var(--an-light-mid)); }
    .route-plane { color: var(--an-mid); flex-shrink: 0; }
    .route-direct { font-size: 0.64rem; color: var(--an-subtext); text-transform: uppercase; letter-spacing: .06em; }

    /* DETALLES */
    .ticket-details {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      padding: 0 32px 22px;
      border-top: 1px solid var(--an-light);
    }

    .detail-item {
      display: flex; flex-direction: column; gap: 3px;
      padding: 14px 16px;
      border-right: 1px solid var(--an-light);
    }

    .detail-item:nth-child(3),
    .detail-item:nth-child(6) { border-right: none; }

    .detail-item:nth-child(1),
    .detail-item:nth-child(2),
    .detail-item:nth-child(3) { border-bottom: 1px solid var(--an-light); }

    .detail-label { font-size: 0.62rem; font-weight: 700; color: var(--an-subtext); text-transform: uppercase; letter-spacing: .07em; }
    .detail-value { font-family: 'Jost', sans-serif; font-size: 0.88rem; color: var(--an-dark); font-weight: 500; }

    .estado-badge {
      background: rgba(45,158,107,.12); color: var(--an-success);
      border-radius: 20px; padding: 2px 10px;
      font-size: 0.7rem; font-weight: 700; width: fit-content;
    }

    /* PERFORADO */
    .ticket-perforated {
      position: relative; display: flex; align-items: center; padding: 0;
    }

    .perf-circle {
      width: 28px; height: 28px; border-radius: 50%;
      background: #e8e8f5;
      position: absolute; top: 50%; transform: translateY(-50%); z-index: 2;
    }

    .perf-left  { left: -14px; }
    .perf-right { right: -14px; }

    .perf-line {
      width: 100%; height: 2px;
      background: repeating-linear-gradient(90deg, var(--an-light-mid) 0, var(--an-light-mid) 8px, transparent 8px, transparent 14px);
    }

    /* STUB */
    .ticket-stub {
      background: linear-gradient(135deg, var(--an-light) 0%, #ebebf8 100%);
      padding: 24px 32px;
      display: flex; align-items: center; justify-content: space-between; gap: 20px;
    }

    .stub-info { display: flex; align-items: center; flex: 1; }

    .stub-item { display: flex; flex-direction: column; gap: 3px; padding: 0 20px; text-align: center; }
    .stub-item-large { padding-left: 0; }

    .stub-label { font-size: 0.62rem; font-weight: 700; color: var(--an-subtext); text-transform: uppercase; letter-spacing: .08em; }

    .stub-value { font-family: 'Jost', sans-serif; font-size: 1.2rem; font-weight: 600; color: var(--an-dark); }
    .stub-value-xl { font-family: 'Archivo Black', sans-serif; font-size: 2.4rem; color: var(--an-dark); letter-spacing: -.02em; }
    .stub-value-code { font-family: 'Archivo Black', sans-serif; font-size: 1.4rem; color: var(--an-mid); letter-spacing: .1em; }

    .stub-divider { width: 1px; height: 44px; background: var(--an-light-mid); flex-shrink: 0; }

    /* QR */
    .stub-qr { display: flex; flex-direction: column; align-items: center; gap: 6px; flex-shrink: 0; }

    .qr-placeholder {
      width: 96px; height: 96px;
      background: var(--an-white); border-radius: var(--radius-sm);
      padding: 8px; border: 1.5px solid var(--an-light-mid);
    }

    .qr-inner { width: 100%; height: 100%; position: relative; display: flex; align-items: center; justify-content: center; flex-direction: column; gap: 2px; }

    .qr-corner { position: absolute; width: 16px; height: 16px; border-color: var(--an-dark); border-style: solid; border-width: 0; }
    .qr-tl { top: 0; left: 0; border-top-width: 3px; border-left-width: 3px; }
    .qr-tr { top: 0; right: 0; border-top-width: 3px; border-right-width: 3px; }
    .qr-bl { bottom: 0; left: 0; border-bottom-width: 3px; border-left-width: 3px; }
    .qr-br { bottom: 0; right: 0; border-bottom-width: 3px; border-right-width: 3px; }

    .qr-dots { display: grid; grid-template-columns: repeat(4, 1fr); gap: 3px; padding: 4px; }
    .qr-dot { width: 7px; height: 7px; border-radius: 1px; }
    .qr-dot-1,.qr-dot-3,.qr-dot-6,.qr-dot-8,.qr-dot-9,.qr-dot-12,.qr-dot-14,.qr-dot-16 { background: var(--an-dark); }
    .qr-dot-2,.qr-dot-4,.qr-dot-5,.qr-dot-7,.qr-dot-10,.qr-dot-11,.qr-dot-13,.qr-dot-15 { background: transparent; }

    .qr-text { font-size: 0.5rem; font-weight: 700; color: var(--an-mid); letter-spacing: .1em; text-align: center; position: absolute; bottom: 1px; }
    .qr-caption { font-size: 0.62rem; color: var(--an-subtext); text-align: center; }

    /* PIE DEL DOCUMENTO */
    .print-doc-footer {
      width: 100%;
      max-width: 880px;
      margin-top: 14px;
      padding-top: 12px;
      border-top: 1px solid var(--an-light-mid);
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .print-doc-footer p {
      font-size: 0.68rem;
      color: var(--an-subtext);
    }

    /* OCULTAR ELEMENTOS NO DESEADOS EN PDF */
    .transaction-card,
    .confirmation-banner,
    .btn-download,
    .btn-new,
    .confirmation-actions { display: none !important; }

    /* Para que el ticket se vea bien en pantalla también */
    .ticket-outer .ticket { margin: 0 auto; }

    @media print {
      body { padding: 0; }
      .print-doc-header,
      .print-doc-footer { display: flex; }
    }
  </style>
</head>
<body>

  <!-- Encabezado del documento impreso -->
  <div class="print-doc-header">
    <span class="print-doc-title">BOLETA DE EMBARQUE — ASTRA NIMBUS AVIATION</span>
    <div class="print-doc-meta">
      <div>Código de reserva: <strong>${codigo}</strong></div>
      <div>Pasajero: <strong>${pasajero}</strong></div>
      <div>Documento generado: ${new Date().toLocaleString('es-CO')}</div>
    </div>
  </div>

  <!-- Ticket clonado del DOM -->
  ${ticketHTML}

  <!-- Pie del documento -->
  <div class="print-doc-footer">
    <p>AstraNimbus Aviation · PAELDAV Corp. · infoastranimbus@gmail.com</p>
    <p>Este documento es válido únicamente con el código de reserva impreso.</p>
  </div>

</body>
</html>`;
  }
}
