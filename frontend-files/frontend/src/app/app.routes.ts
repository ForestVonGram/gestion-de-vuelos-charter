import { Routes } from '@angular/router';
import { LoginComponent } from './pages/auth/login/login.component';

import { LandingComponent } from './pages/landing/landing.component';
import { RegisterComponent } from './pages/auth/register/register.component';
import { ClientDashboardComponent } from './pages/cliente/dashboard_cliente/dashboard-cliente.component';
import {authGuard} from './guards/auth.guard';
import {adminGuard} from './guards/admin.guard';
import {AdminDashboardComponent} from './pages/administrador/admin-dashboard/admin-dashboard.component';
import {PreciosComponent} from './pages/precios/precios.component';
import { Tripulante } from './pages/personal/tripulante/tripulante';
import { Tripulacion } from './pages/personal/tripulacion/tripulacion';
import { Vuelos } from './pages/personal/vuelos/vuelos';
import { Certificados } from './pages/personal/certificados/certificados';
import { Reportes } from './pages/personal/reportes/reportes';


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
    path: 'tripulante',
    component: Tripulante,
    canActivate: []
  },
  {
    path: 'tripulacion',
    component: Tripulacion,
    canActivate: []
  },
  {
    path: 'vuelos',
    component: Vuelos,
    canActivate: []
  },
  {
    path: 'certificados',
    component: Certificados,
    canActivate: []
  },
  {
    path: 'reportes',
    component: Reportes,
    canActivate: []
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
    canActivate: [], // Esta línea bloquea a los que no son ADMIN
    children: [
      {
        path: 'dashboard', // La ruta completa será: /admin/dashboard
        component: AdminDashboardComponent
      }
    ]
  },
];
