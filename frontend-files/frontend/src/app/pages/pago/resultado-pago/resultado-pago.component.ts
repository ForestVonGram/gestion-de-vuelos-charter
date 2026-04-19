import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule, Router } from '@angular/router';
import { PagoService } from '../../../services/pago/pago.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-resultado-pago',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './resultado-pago.component.html',
  styleUrls: ['./resultado-pago.component.css']
})
export class ResultadoPagoComponent implements OnInit {
  status: string = '';
  paymentId: string = '';
  preferenceId: string = '';
  externalReference: string = '';
  vueloId: number | null = null;
  loading: boolean = true;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private pagoService: PagoService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.status = params['status'];
      this.paymentId = params['payment_id'];
      this.preferenceId = params['preference_id'];
      this.externalReference = params['external_reference'];

      if (this.status === 'approved') {
        this.confirmarPago();
      } else {
        this.loading = false;
        if (this.externalReference) {
          this.pagoService.obtenerPagoPorId(Number(this.externalReference)).subscribe(p => {
            this.vueloId = p.vueloId;
          });
        }
      }
    });
  }

  confirmarPago(): void {
    const pagoId = Number(this.externalReference);
    
    if (pagoId) {
      this.pagoService.confirmarPago(pagoId, this.paymentId).subscribe({
        next: (pagoActualizado) => {
          this.vueloId = pagoActualizado.vueloId;
          this.loading = false;
          Swal.fire({
            title: '¡Pago Exitoso!',
            text: 'Tu vuelo ha sido confirmado correctamente.',
            icon: 'success',
            confirmButtonText: 'Ver mi Boleta',
            confirmButtonColor: '#007bff'
          }).then(() => {
            this.router.navigate(['/agendar-vuelo'], { queryParams: { id: this.vueloId } });
          });
        },
        error: (err) => {
          console.error('Error confirmando pago:', err);
          this.loading = false;
          Swal.fire('Error', 'No pudimos registrar tu pago localmente, pero Mercado Pago confirmó la transacción. Contacta a soporte con tu ID de pago.', 'error');
        }
      });
    } else {
      this.loading = false;
    }
  }
}
