import { Routes } from '@angular/router';
import { LoginComponent } from './pages/auth/login/login.component';
import { LandingComponent } from './pages/landing/landing.component';
import { RegisterComponent } from './pages/auth/register/register.component';
import { ClientDashboardComponent } from './pages/cliente/dashboard_cliente/dashboard-cliente.component';
import { MyProfile } from './shared/my-profile/my-profile';
import {authGuard} from './guards/auth.guard';
import {adminGuard} from './guards/admin.guard';
import {PreciosComponent} from './pages/precios/precios.component';
import { TerminosCondicionesComponent } from './pages/auth/terminos_condiciones/terminos_condiciones.component';

import { MantenimientoComponent } from './pages/operador/mantenimiento/mantenimiento.component';
import { MantenimientoDetalleComponent } from './pages/operador/mantenimiento_detalle/mantenimiento_detalle.component';
import { MantenimientoFormComponent } from './pages/operador/mantenimiento_form/mantenimiento_form.component';
import { MantenimientoOfrecidoComponent } from './pages/operador/mantenimiento_ofrecido/mantenimiento_ofrecido.component';

import { TripulanteComponent } from './pages/personal/tripulante/tripulante.component';
import { TripulacionComponent } from './pages/personal/tripulacion/tripulacion.component';
import { VuelosComponent } from './pages/personal/vuelos/vuelos.component';
import { CertificadosComponent } from './pages/personal/certificados/certificados.component';
import { ReportesComponent } from './pages/personal/reportes/reportes.component';

import {AdminDashboardComponent} from './pages/administrador/admin-dashboard/admin-dashboard.component';
import { VuelosAdminComponent } from './pages/administrador/vuelos-admin/vuelos-admin.component';
import { EstadisticasAdminComponent } from './pages/administrador/estadisticas-admin/estadisticas-admin.component';
import { FlotaAereaAdminComponent } from './pages/administrador/flota-aerea-admin/flota-aerea-admin.component';
import { NominaAdminComponent } from './pages/administrador/nomina-admin/nomina-admin.component';
import { ReportesAdminComponent } from './pages/administrador/reportes-admin/reportes-admin.component';

export const routes: Routes = [
  {
    path: '',
    component: LandingComponent
  },
  {
    path: 'auth',
    children: [
      {
        path: 'login',
        component: LoginComponent
      },
      {
        path: 'register',
        component: RegisterComponent
      }
    ]
  },
  {
    path: 'dashboard',
    component: ClientDashboardComponent,
    canActivate: [authGuard]
  },
  {
    path: 'terminos-condiciones',
    component: TerminosCondicionesComponent
  },
  {
    path: 'precios',
    component: PreciosComponent,
    canActivate: [authGuard]
  },
  {
    path: 'tripulante',
    component: TripulanteComponent,
    canActivate: []
  },
  {
    path: 'tripulacion',
    component: TripulacionComponent,
    canActivate: []
  },
  {
    path: 'vuelos',
    component: VuelosComponent,
    canActivate: []
  },
  {
    path: 'certificados',
    component: CertificadosComponent,
    canActivate: []
  },
  {
    path: 'reportes',
    component: ReportesComponent,
    canActivate: []
  },
  {
    path: 'admin',
    canActivate: [], // Esta línea bloquea a los que no son ADMIN
    children: [
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      },
      {
        path: 'dashboard', // La ruta completa será: /admin/dashboard
        component: AdminDashboardComponent
      },
      {
        path: 'vuelos-admin',
        component: VuelosAdminComponent
      },
      {
        path: 'estadisticas',
        component: EstadisticasAdminComponent
      },

      {
        path: 'flota',
        component: FlotaAereaAdminComponent
      },
      {
        path: 'nomina',
        component: NominaAdminComponent
      },
      {
        path: 'reportes',
        component: ReportesAdminComponent
      }
    ]
  },
  {
    path: 'operador',
    children: [
      {
        path: 'mantenimiento',
        component: MantenimientoComponent
      },
      {
        path: 'mantenimiento/nuevo',
        component: MantenimientoFormComponent
      },
      {
        path: 'mantenimientos/ofrecidos',
        component: MantenimientoOfrecidoComponent
      },
      {
        path: 'mantenimiento/:id',
        component: MantenimientoDetalleComponent
      }
    ]
  },
  {
    path: 'profile',
    component: MyProfile
  }
  
];
