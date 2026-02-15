import { Routes } from '@angular/router';
import { LoginComponent } from './pages/auth/login/login.component';
import { MantenimientoComponent } from './pages/operador/mantenimiento/mantenimiento.component';
import { MantenimientoDetalleComponent } from './pages/operador/mantenimiento_detalle/mantenimiento_detalle.component';
import { MantenimientoFormComponent } from './pages/operador/mantenimiento_form/mantenimiento_form.component';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'auth/login',
    pathMatch: 'full'
  },
  {
    path: 'auth',
    children: [
      {
        path: 'login',
        component: LoginComponent
      }
    ]
  },
  {
    path: 'mantenimientos',
    children: [
      {
        path: '',
        component: MantenimientoComponent
      },
      {
        path: 'nuevo',
        component: MantenimientoFormComponent
      },
      {
        path: 'editar/:id',
        component: MantenimientoFormComponent
      },
      {
        path: ':id',
        component: MantenimientoDetalleComponent
      }
    ]
  },
  {
    path: '**',
    redirectTo: 'auth/login'
  }
];
