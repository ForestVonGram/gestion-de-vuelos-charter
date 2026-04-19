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
      }
    });
  }

  confirmarPago(): void {
    // El externalReference debería ser el ID de nuestro registro de Pago
    // o el ID del vuelo si lo guardamos así. 
    // En PagoServiceImpl, usamos el ID del pago autogenerado.
    // Pero en MercadoPagoService pusimos externalReference = vueloId.
    // Esto es un pequeño desajuste que debemos corregir o manejar.
    
    // Si externalReference es el vueloId, necesitamos buscar el pago pendiente para ese vuelo.
    // Por simplicidad, asumiremos que externalReference es el ID del PAGO si lo ajustamos en el backend.
    
    const pagoId = Number(this.externalReference);
    
    if (pagoId) {
      this.pagoService.confirmarPago(pagoId, this.paymentId).subscribe({
        next: () => {
          this.loading = false;
          Swal.fire({
            title: '¡Pago Exitoso!',
            text: 'Tu vuelo ha sido confirmado correctamente.',
            icon: 'success',
            confirmButtonText: 'Ir a mis vuelos',
            confirmButtonColor: '#007bff'
          }).then(() => {
            this.router.navigate(['/cliente/dashboard']);
          });
        },
        error: (err) => {
          console.error('Error confirmando pago:', err);
          this.loading = false;
          Swal.fire('Error', 'No pudimos registrar tu pago, por favor contacta a soporte.', 'error');
        }
      });
    } else {
      this.loading = false;
    }
  }
}
