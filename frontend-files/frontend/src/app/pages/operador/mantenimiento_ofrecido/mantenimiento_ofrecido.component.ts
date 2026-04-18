import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../services/auth/auth.service';
import {AccesibilidadComponent} from '../../../shared/accesibilidad/accesibilidad.component';
import {ChatbotWidgetComponent} from '../../../shared/chatbot-widget/chatbot-widget.component';
import {WhatsAppButtonComponent} from '../../../shared/whatsapp-button/whatsapp-button.component';

// Interfaz interna para definir la estructura de los servicios ofrecidos
interface TipoMantenimiento {
  value: string;
  label: string;
  icon: string;
  desc: string;
  features: string[]; // Lista de características específicas de cada servicio
}

@Component({
  selector: 'app-mantenimiento-ofrecido',
  templateUrl: './mantenimiento_ofrecido.component.html',
  styleUrls: ['./mantenimiento_ofrecido.component.css'],
  standalone: true,
  imports: [CommonModule, RouterModule, AccesibilidadComponent, ChatbotWidgetComponent, WhatsAppButtonComponent]
})
export class MantenimientoOfrecidoComponent implements OnInit {
  // --- Propiedades de Usuario y Estado ---
  userName: string = 'Operador';
  userEmail: string = '';
  isDropdownOpen: boolean = false;
  loading: boolean = false;

  // Catálogo de servicios basado en los Enums del backend para mantener la consistencia
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
    this.cargarDatosUsuario(); // Al iniciar, recuperamos los datos de la sesión activa
  }

  // Extrae la información del usuario desde el servicio de autenticación
  cargarDatosUsuario(): void {
    const currentUser = this.authService.currentUserValue;
    if (currentUser) {
      this.userName = currentUser.nombreCompleto || 'Operador';
      this.userEmail = currentUser.email || '';
    }
  }

  // Maneja la apertura del menú de perfil en el header
  toggleDropdown(): void {
    this.isDropdownOpen = !this.isDropdownOpen;
  }

  // Finaliza la sesión y limpia el almacenamiento local
  logout(): void {
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }

  // Inicia el flujo de solicitud enviando el tipo de mantenimiento como parámetro de consulta
  solicitarMantenimiento(tipo: string): void {
    console.log('Solicitando mantenimiento:', tipo);
    // Redirige al formulario de creación pasando el tipo seleccionado mediante QueryParams
    this.router.navigate(['/operador/mantenimiento/nuevo'], {
      queryParams: { tipo: tipo }
    });
  }

  // Proporciona una vía de contacto alternativa para casos no estándar
  contactar(): void {
    console.log('Contactando para mantenimiento personalizado');
    alert('Por favor, contáctenos al correo: servicios@astranimbus.com');
  }
}
