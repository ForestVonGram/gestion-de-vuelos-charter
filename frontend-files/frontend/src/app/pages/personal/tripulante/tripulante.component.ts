import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HeaderTripulante } from "../../../shared/header-tripulante/header-tripulante.component";
import { ProfileService } from '../../../services/auth/profile-service';
import { ProfileDto } from '../../../models/users/profile-dto';
import { ChangeDetectorRef } from '@angular/core';
import { AccesibilidadComponent} from '../../../shared/accesibilidad/accesibilidad.component';
import {ChatbotWidgetComponent} from '../../../shared/chatbot-widget/chatbot-widget.component';
import {WhatsAppButtonComponent} from '../../../shared/whatsapp-button/whatsapp-button.component';

@Component({
  selector: 'app-tripulante',
  imports: [CommonModule, HeaderTripulante, AccesibilidadComponent, ChatbotWidgetComponent, WhatsAppButtonComponent],
  templateUrl: './tripulante.component.html',
  styleUrls: ['./tripulante.component.css']
})
export class TripulanteComponent implements OnInit {
  profile: ProfileDto | null = null;
  tripulanteNombre: string = '';
  fechaActual: Date = new Date();
  tabActivo: string = 'vuelos';

  // Datos del tripulante (ahora vendrán del profile)
  totalVuelosMes: number = 0;
  horasVueloMes: number = 0;
  certificadosVigentes: number = 0;
  reportesPendientes: number = 2; // Este podría venir de otro servicio

  // Vuelos
  proximosVuelos: any[] = [];
  historialVuelos: any[] = [];

  // Reportes
  reportes: any[] = [];

  // Certificaciones
  certificaciones: any[] = [];

  constructor(
    private profileService: ProfileService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    const user = this.profileService.currentUserValue;

    if (user) {
      this.cargarDatosTripulante(user.userId);
    } else {
      console.error('No hay usuario autenticado');
      this.tripulanteNombre = 'Tripulante';
    }

    // Cargamos los demás datos (estos podrían venir de servicios específicos)
    this.cargarVuelos();
    this.cargarReportes();
    this.cargarCertificaciones();
  }

  cargarDatosTripulante(id: number) {
    this.profileService.getProfile(id).subscribe({
      next: (profile) => {
        this.profile = profile;

        // Construimos el nombre completo
        if (profile.usuarioNombre && profile.usuarioApellido) {
          this.tripulanteNombre = `${profile.usuarioNombre} ${profile.usuarioApellido}`;
        } else {
          this.tripulanteNombre = profile.usuarioNombre || 'Tripulante';
        }

        // Actualizamos las estadísticas con los datos reales del perfil
        this.horasVueloMes = profile.horasVueloMes || 0;

        // Si el perfil tiene información de certificados, la actualizamos
        // Esto dependerá de cómo tengas modelado el ProfileDto
        // this.certificadosVigentes = profile.certificados?.length || 0;

        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error al cargar perfil:', error);
        this.tripulanteNombre = 'Tripulante';
      }
    });
  }

  cargarVuelos() {
    // Aquí idealmente usarías un FlightService que también use el userId
    // Por ahora mantenemos los datos simulados
    this.proximosVuelos = [
      {
        origen: 'Madrid (MAD)',
        destino: 'Barcelona (BCN)',
        fecha: new Date(2024, 10, 15),
        horaSalida: '08:30',
        horaLlegada: '09:45',
        asiento: '12A',
        estado: 'Confirmado'
      },
      {
        origen: 'Barcelona (BCN)',
        destino: 'París (CDG)',
        fecha: new Date(2024, 10, 18),
        horaSalida: '14:20',
        horaLlegada: '16:05',
        asiento: '8B',
        estado: 'Pendiente'
      },
      {
        origen: 'París (CDG)',
        destino: 'Roma (FCO)',
        fecha: new Date(2024, 10, 22),
        horaSalida: '11:45',
        horaLlegada: '13:30',
        asiento: '15C',
        estado: 'Confirmado'
      }
    ];

    this.historialVuelos = [
      {
        fecha: new Date(2024, 9, 28),
        origen: 'Londres (LHR)',
        destino: 'Madrid (MAD)',
        duracion: '2h 15m',
        rol: 'Tripulante de Cabina'
      },
      {
        fecha: new Date(2024, 9, 25),
        origen: 'Madrid (MAD)',
        destino: 'Lisboa (LIS)',
        duracion: '1h 30m',
        rol: 'Tripulante de Cabina'
      },
      {
        fecha: new Date(2024, 9, 22),
        origen: 'Berlín (BER)',
        destino: 'Madrid (MAD)',
        duracion: '3h 0m',
        rol: 'Tripulante Senior'
      }
    ];
  }

  cargarReportes() {
    // Idealmente estos vendrían de un servicio específico
    this.reportes = [
      {
        id: 1,
        titulo: 'Reporte de Vuelo MAD-BCN',
        descripcion: 'Incidencias y observaciones del vuelo del 28/10/2024',
        fecha: new Date(2024, 9, 28),
        icono: 'fa-file-alt',
        tipo: 'vuelo'
      },
      {
        id: 2,
        titulo: 'Reporte Mensual Octubre',
        descripcion: 'Resumen de actividades y horas de vuelo',
        fecha: new Date(2024, 9, 31),
        icono: 'fa-chart-line',
        tipo: 'mensual'
      },
      {
        id: 3,
        titulo: 'Incidente Técnico',
        descripcion: 'Reporte de incidencia en sistema de entretenimiento',
        fecha: new Date(2024, 9, 25),
        icono: 'fa-exclamation-triangle',
        tipo: 'incidente'
      }
    ];
  }

  cargarCertificaciones() {
    // Idealmente estos vendrían de un servicio específico de certificaciones
    // y podrían filtrarse por el userId
    this.certificaciones = [
      {
        id: 1,
        nombre: 'Certificación de Seguridad Aérea',
        emisor: 'Agencia Europea de Seguridad Aérea',
        fechaExpiracion: new Date(2025, 5, 30),
        expirado: false,
        archivo: 'seguridad_aerea.pdf'
      },
      {
        id: 2,
        nombre: 'Primeros Auxilios en Vuelo',
        emisor: 'Cruz Roja Internacional',
        fechaExpiracion: new Date(2024, 11, 15),
        expirado: true,
        archivo: 'primeros_auxilios.pdf'
      },
      {
        id: 3,
        nombre: 'Idiomas: Inglés Aeronáutico',
        emisor: 'ICAO Nivel 4',
        fechaExpiracion: new Date(2025, 8, 20),
        expirado: false,
        archivo: 'icao_ingles.pdf'
      },
      {
        id: 4,
        nombre: 'Manejo de Emergencias',
        emisor: 'Centro de Entrenamiento Aéreo',
        fechaExpiracion: new Date(2025, 2, 10),
        expirado: false,
        archivo: 'emergencias.pdf'
      }
    ];

    // Calculamos certificados vigentes
    this.certificadosVigentes = this.certificaciones.filter(c => !c.expirado).length;
  }

  cambiarTab(tab: string) {
    this.tabActivo = tab;
  }

  verTodosVuelos() {
    console.log('Ver todos los vuelos');
    // Navegar a la página de vuelos completa
  }

  descargarReporte(reporte: any) {
    console.log('Descargando reporte:', reporte);
    // Lógica para descargar reporte
  }

  generarReporte() {
    console.log('Generar nuevo reporte');
    // Lógica para crear nuevo reporte
  }

  verCertificado(certificado: any) {
    console.log('Ver certificado:', certificado);
    // Lógica para visualizar certificado
  }

  descargarCertificado(certificado: any) {
    console.log('Descargando certificado:', certificado);
    // Lógica para descargar certificado
  }
}
