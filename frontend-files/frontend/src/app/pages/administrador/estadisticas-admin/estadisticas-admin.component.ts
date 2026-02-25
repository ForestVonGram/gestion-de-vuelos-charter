import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService, User } from '../../../services/auth/auth.service';

// --- DTOs (Espejo de tu backend Java) ---
export interface MetricasVuelosDTO {
  vuelosTotales: number;
  vuelosCompletados: number;
  vuelosEnProceso: number;
  vuelosCancelados: number;
  vuelosProgramados: number;
  porcentajeComplecion: number;
  ingresoTotalVuelos: number;
}

export interface MetricasPersonalDTO {
  personalTotal: number;
  personalActivo: number;
  personalEnLicencia: number;
  tripulantesTotal: number;
  tripulantesDisponibles: number;
  tripulantesEnVuelo: number;
  horasTotalPersonal: number;
  horasPromedioPersonal: number;
}

export interface MetricasFlotaDTO {
  aeronavesTotales: number;
  aeronavesActivas: number;
  aeronavesEnMantenimiento: number;
  aeronavesDisponibles: number;
  porcentajeDisponibilidad: number;
  horasTotalVuelo: number;
  horasPromedioPorAeronave: number;
}

export interface MetricasDTO {
  fechaActualizacion: string | Date;
  metricasVuelos: MetricasVuelosDTO;
  metricasFlota: MetricasFlotaDTO;
  metricasPersonal: MetricasPersonalDTO;
  rentabilidadPromedio: number;
  ocupacionPromedio: number;
}

@Component({
  selector: 'app-estadisticas-admin',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './estadisticas-admin.component.html',
  styleUrls: ['./estadisticas-admin.component.css']
})
export class EstadisticasAdminComponent implements OnInit {

  currentUser: User | null = null;
  metricas!: MetricasDTO;

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    this.currentUser = this.authService.currentUserValue;
    this.cargarDatosSimulados();
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }

  // Simulamos la respuesta de tu backend
  cargarDatosSimulados(): void {
    this.metricas = {
      fechaActualizacion: new Date(),
      rentabilidadPromedio: 24.5,
      ocupacionPromedio: 85.2,
      metricasVuelos: {
        vuelosTotales: 1250,
        vuelosCompletados: 1100,
        vuelosEnProceso: 12,
        vuelosProgramados: 108,
        vuelosCancelados: 30,
        porcentajeComplecion: 88.0,
        ingresoTotalVuelos: 4500000.00
      },
      metricasFlota: {
        aeronavesTotales: 15,
        aeronavesActivas: 12,
        aeronavesEnMantenimiento: 2,
        aeronavesDisponibles: 10,
        porcentajeDisponibilidad: 80.0,
        horasTotalVuelo: 15420.5,
        horasPromedioPorAeronave: 1028.0
      },
      metricasPersonal: {
        personalTotal: 45,
        personalActivo: 42,
        personalEnLicencia: 3,
        tripulantesTotal: 30,
        tripulantesDisponibles: 12,
        tripulantesEnVuelo: 18,
        horasTotalPersonal: 8500.0,
        horasPromedioPersonal: 188.8
      }
    };
  }
}
