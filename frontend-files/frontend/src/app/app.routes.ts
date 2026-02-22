import { Routes } from '@angular/router';
import { LoginComponent } from './pages/auth/login/login.component';
import { LandingComponent } from './pages/landing/landing.component';
import { RegisterComponent } from './pages/auth/register/register.component';
import { ClientDashboardComponent } from './pages/cliente/dashboard_cliente/dashboard-cliente.component';
import { MantenimientoComponent } from './pages/operador/mantenimiento/mantenimiento.component';
import { MantenimientoDetalleComponent } from './pages/operador/mantenimiento_detalle/mantenimiento_detalle.component';
import { MantenimientoFormComponent } from './pages/operador/mantenimiento_form/mantenimiento_form.component';
import { AdminDashboardComponent } from './pages/administrador/admin-dashboard/admin-dashboard.component';
import { PreciosComponent } from './pages/precios/precios.component';
import { MantenimientoOfrecidoComponent } from './pages/operador/mantenimiento_ofrecido/mantenimiento_ofrecido.component';
import { TerminosCondicionesComponent } from './pages/auth/terminos_condiciones/terminos_condiciones.component';

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
    component: ClientDashboardComponent
  },
  {
    path: 'terminos-condiciones',
    component: TerminosCondicionesComponent
  },
  {
    path: 'precios',
    component: PreciosComponent
  },
  {
    path: 'admin',
    children: [
      {
        path: 'dashboard',
        component: AdminDashboardComponent
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
  }
];
