import { Routes } from '@angular/router';
import { LoginComponent } from './pages/auth/login/login.component';
import { LandingComponent } from './pages/landing/landing.component';
import { RegisterComponent } from './pages/auth/register/register.component';
import { ClientDashboardComponent } from './pages/cliente/dashboard_cliente/dashboard-cliente.component';
import {authGuard} from './guards/auth.guard';
import {adminGuard} from './guards/admin.guard';
import {AdminDashboardComponent} from './pages/administrador/admin-dashboard/admin-dashboard.component';
import {PreciosComponent} from './pages/precios/precios.component';

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
        path: 'dashboard', // La ruta completa será: /admin/dashboard
        component: AdminDashboardComponent
      }
    ]
  },
];
