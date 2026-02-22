import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../services/auth/auth.service';

interface TipoMantenimiento {
  value: string;
  label: string;
  icon: string;
  desc: string;
  features: string[];
}

@Component({
  selector: 'app-mantenimiento-ofrecido',
  templateUrl: './mantenimiento_ofrecido.component.html',
  styleUrls: ['./mantenimiento_ofrecido.component.css'],
  standalone: true,
  imports: [CommonModule, RouterModule]
})
export class MantenimientoOfrecidoComponent implements OnInit {
  // Propiedades para el dropdown de usuario
  userName: string = 'Operador';
  userEmail: string = '';
  isDropdownOpen: boolean = false;
  loading: boolean = false;

  // Tipos de mantenimiento según el enum del backend
  tiposMantenimiento: TipoMantenimiento[] = [
    {
      value: 'PREVENTIVO',
      label: 'Preventivo',
      icon: '🔧',
      desc: 'Mantenimiento programado para prevenir fallas y optimizar el rendimiento',
      features: ['Inspección programada', 'Lubricación de componentes', 'Cambio de filtros', 'Ajustes preventivos']
    },
    {
      value: 'CORRECTIVO',
      label: 'Correctivo',
      icon: '⚙️',
      desc: 'Reparación de fallas detectadas durante la operación o inspecciones',
      features: ['Diagnóstico de fallas', 'Reparación de componentes', 'Solución inmediata', 'Pruebas post-reparación']
    },
    {
      value: 'REPOSTAJE',
      label: 'Repostaje',
      icon: '⛽',
      desc: 'Servicio de carga de combustible con los más altos estándares',
      features: ['Combustible de alta calidad', 'Control de calidad', 'Medición precisa', 'Certificación incluida']
    },
    {
      value: 'INSPECCION',
      label: 'Inspección',
      icon: '🔍',
      desc: 'Revisiones detalladas de todos los sistemas de la aeronave',
      features: ['Inspección visual', 'Pruebas no destructivas', 'Verificación de sistemas', 'Certificación técnica']
    }
  ];

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.cargarDatosUsuario();
  }

  cargarDatosUsuario(): void {
    const currentUser = this.authService.currentUserValue;
    if (currentUser) {
      this.userName = currentUser.nombreCompleto || 'Operador';
      this.userEmail = currentUser.email || '';
    }
  }

  toggleDropdown(): void {
    this.isDropdownOpen = !this.isDropdownOpen;
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }

  solicitarMantenimiento(tipo: string): void {
    console.log('Solicitando mantenimiento:', tipo);
    // Aquí iría la lógica para solicitar el mantenimiento
    // Por ahora solo redirigimos al formulario de nuevo mantenimiento con el tipo preseleccionado
    this.router.navigate(['/operador/mantenimiento/nuevo'], {
      queryParams: { tipo: tipo }
    });
  }

  contactar(): void {
    console.log('Contactando para mantenimiento personalizado');
    // Aquí iría la lógica para contactar
    // Por ejemplo, abrir un modal o redirigir a una página de contacto
    alert('Por favor, contáctenos al correo: servicios@astranimbus.com');
  }
}
