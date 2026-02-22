import { Routes } from '@angular/router';
import { LoginComponent } from './pages/auth/login/login.component';
import { LandingComponent } from './pages/landing/landing.component';
import { RegisterComponent } from './pages/auth/register/register.component';
import { ClientDashboardComponent } from './pages/cliente/dashboard_cliente/dashboard-cliente.component';
import {authGuard} from './guards/auth.guard';
import {adminGuard} from './guards/admin.guard';
import {PreciosComponent} from './pages/precios/precios.component';

import {AdminDashboardComponent} from './pages/administrador/admin-dashboard/admin-dashboard.component';
//import { VuelosAdminComponent } from './pages/administrador/vuelos-admin/vuelos-admin.component';
import { EstadisticasAdminComponent } from './pages/administrador/estadisticas-admin/estadisticas-admin.component';
import { FlotaAereaAdminComponent } from './pages/administrador/flota-aerea-admin/flota-aerea-admin.component';
//import { NominaAdminComponent } from './pages/administrador/nomina-admin/nomina-admin.component';
//import { ReportesAdminComponent } from './pages/administrador/reportes-admin/reportes-admin.component';

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
    path: 'precios',
    component: PreciosComponent,
    canActivate: [authGuard]
  },
  {
    path: 'admin',
    canActivate: [adminGuard], // Esta línea bloquea a los que no son ADMIN
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
      /*
      {
        path: 'vuelos',
        component: VuelosAdminComponent
      },

      */
      {
        path: 'estadisticas',
        component: EstadisticasAdminComponent
      },

      {
        path: 'flota',
        component: FlotaAereaAdminComponent
      },
      /*
      {
        path: 'nomina',
        component: NominaAdminComponent
      },
      {
        path: 'reportes',
        component: ReportesAdminComponent
      }

       */
    ]
  },
];
