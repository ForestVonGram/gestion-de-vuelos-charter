import { Component, OnInit, ViewChild, ElementRef, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { VueloService, VueloDTO } from '../../services/vuelos/vuelo.service';
import { AccesibilidadComponent } from '../../shared/accesibilidad/accesibilidad.component';
import { ChatbotWidgetComponent } from '../../shared/chatbot-widget/chatbot-widget.component';
import { WhatsAppButtonComponent } from '../../shared/whatsapp-button/whatsapp-button.component';

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
  imports: [CommonModule, RouterModule, AccesibilidadComponent, ChatbotWidgetComponent, WhatsAppButtonComponent],
  templateUrl: './boleta.component.html',
  styleUrls: ['./boleta.component.css']
})
export class BoletaComponent implements OnInit {
  @ViewChild('boletaRef') boletaRef!: ElementRef;

  boletaData: BoletaData | null = null;
  cargando = true;
  vueloId: number | null = null;

  constructor(
    private vueloService: VueloService,
    private route: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.vueloId = +params['id'];
        this.cargarVuelo();
      } else {
        this.router.navigate(['/dashboard']);
      }
    });
  }

  private cargarVuelo(): void {
    if (!this.vueloId) return;
    this.cargando = true;
    this.vueloService.getVueloById(this.vueloId).subscribe({
      next: (vuelo) => {
        // Nota: Los datos del pasajero (documento, tipo) no vienen en el DTO del vuelo.
        // Idealmente deberías tener un endpoint que devuelva el pasajero asociado.
        // Por ahora simulamos con valores por defecto.
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
          claseServicio: 'Ejecutiva', // Debería venir en el vuelo (campo claseServicio)
          codigoQR: this.generarCodigoQR(vuelo.id),
          fechaEmision: vuelo.fechaSolicitud,
          montoTotal: vuelo.costoEstimado || 0,
          metodoPago: 'No especificado',
          transaccionId: this.generarTransaccionId()
        };
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
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Archivo+Black&family=Jost:wght@300;400;600&family=Libre+Franklin:wght@400;600;700&display=swap" rel="stylesheet" />
  <style>
    /* RESET Y VARIABLES */
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
    @page { size: A4 landscape; margin: 12mm 16mm; }
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
    .ticket-outer { width: 100%; max-width: 880px; }
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
    /* PIE */
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
    .ticket-outer .ticket { margin: 0 auto; }
    @media print {
      body { padding: 0; }
      .print-doc-header,
      .print-doc-footer { display: flex; }
    }
  </style>
</head>
<body>
  <div class="print-doc-header">
    <span class="print-doc-title">BOLETA DE EMBARQUE — ASTRA NIMBUS AVIATION</span>
    <div class="print-doc-meta">
      <div>Código de reserva: <strong>${codigo}</strong></div>
      <div>Pasajero: <strong>${pasajero}</strong></div>
      <div>Documento generado: ${new Date().toLocaleString('es-CO')}</div>
    </div>
  </div>
  ${ticketHTML}
  <div class="print-doc-footer">
    <p>AstraNimbus Aviation · PAELDAV Corp. · infoastranimbus@gmail.com</p>
    <p>Este documento es válido únicamente con el código de reserva impreso.</p>
  </div>
</body>
</html>`;
  }

  volverAlDashboard(): void {
    this.router.navigate(['/dashboard']);
  }
}
