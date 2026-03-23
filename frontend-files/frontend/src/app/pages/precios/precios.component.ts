import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import {AccesibilidadComponent} from '../../shared/accesibilidad/accesibilidad.component';

interface Plan {
  name: string;
  price: string;
  period: string;
  description: string;
  features: string[];
  isPopular: boolean;
  buttonText: string;
}

@Component({
  selector: 'app-precios',
  standalone: true,
  imports: [CommonModule, RouterModule, AccesibilidadComponent],
  templateUrl: './precios.component.html',
  styleUrls: ['./precios.component.css']
})
export class PreciosComponent {

  plans: Plan[] = [
    {
      name: 'Básico',
      price: '$1,200',
      period: '/ hora de vuelo',
      description: 'Ideal para viajes cortos y escapadas de fin de semana.',
      features: [
        'Aeronaves Piper Seneca',
        'Hasta 4 pasajeros',
        'Bebidas de cortesía',
        'Equipaje ligero (10kg)',
        'Atención estándar'
      ],
      isPopular: false,
      buttonText: 'Elegir Ligero'
    },
    {
      name: 'Ejecutivo',
      price: '$2,500',
      period: '/ hora de vuelo',
      description: 'La opción preferida para negocios y confort superior.',
      features: [
        'Aeronaves Baron G58',
        'Hasta 6 pasajeros',
        'Catering premium incluido',
        'Equipaje estándar (23kg)',
        'Wi-Fi a bordo',
        'Lounge de espera VIP'
      ],
      isPopular: true, // Este plan se resaltará
      buttonText: 'Elegir Ejecutivo'
    },
    {
      name: 'Elite',
      price: '$4,800',
      period: '/ hora de vuelo',
      description: 'Lujo absoluto y privacidad total sin compromisos.',
      features: [
        'Jets Citation o Phenom',
        'Hasta 9 pasajeros',
        'Chef privado a bordo',
        'Equipaje ilimitado',
        'Transfer en helicóptero',
        'Concierge 24/7'
      ],
      isPopular: false,
      buttonText: 'Contactar Ventas'
    }
  ];
}
