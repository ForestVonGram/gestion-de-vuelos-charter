import { Routes } from '@angular/router';
import { MantenimientoComponent } from './pages/operador/mantenimiento/mantenimiento';

export const routes: Routes = [
  {
    path: 'mantenimientos',
    component: MantenimientoComponent
  },
  {
    path: '',
    redirectTo: 'mantenimientos',
    pathMatch: 'full'
  }
];
