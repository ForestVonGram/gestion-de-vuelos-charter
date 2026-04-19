import { Routes } from '@angular/router';
import { LoginComponent } from './pages/auth/login/login.component';
import { LandingComponent } from './pages/landing/landing.component';
import { RegisterComponent } from './pages/auth/register/register.component';
import { RecuperarSolicitudComponent } from './pages/auth/solicitud_recuperacion/solicitud_recuperacion.component';
import { RecuperarContraseniaComponent } from './pages/auth/recuperar_contraseña/recuperar_contraseña.component';

import { ClientDashboardComponent } from './pages/cliente/dashboard_cliente/dashboard-cliente.component';
import { BoletaComponent} from './pages/boleta/boleta.component';
import { authGuard } from './guards/auth.guard';
import { adminGuard } from './guards/admin.guard';

import { PreciosComponent } from './pages/precios/precios.component';
import { TerminosCondicionesComponent } from './pages/auth/terminos_condiciones/terminos_condiciones.component';
import { PoliticaPrivacidadComponent} from './pages/auth/politica_privacidad/politica_privacidad.component';
import { SobreNosotrosComponent} from './pages/informacion_general/sobre_nosotros/sobre_nosotros.component';
import { NuestraFlotaComponent} from './pages/informacion_general/nuestra_flota/nuestra_flota.component';
import { TrabajaConNosotrosComponent} from './pages/informacion_general/trabaja_con_nosotros/trabaja_con_nosotros.component';

import { MantenimientoComponent } from './pages/operador/mantenimiento/mantenimiento.component';
import { MantenimientoDetalleComponent } from './pages/operador/mantenimiento_detalle/mantenimiento_detalle.component';
import { MantenimientoFormComponent } from './pages/operador/mantenimiento_form/mantenimiento_form.component';
import { MantenimientoOfrecidoComponent } from './pages/operador/mantenimiento_ofrecido/mantenimiento_ofrecido.component';
import { DashboardOperadorJComponent } from './pages/operador_jefe/dashboard/dashboard_operadorj.component';

import { TripulanteComponent } from './pages/personal/tripulante/tripulante.component';
import { TripulacionComponent} from './pages/personal/tripulacion/tripulacion.component';
import { VuelosComponent } from './pages/personal/vuelos/vuelos.component';
import { CertificadosComponent } from './pages/personal/certificados/certificados.component';
import { ReportesComponent} from './pages/personal/reportes/reportes.component';
import { ProfileComponent} from './pages/cliente/profile/profile.component';

import { AdminDashboardComponent } from './pages/administrador/admin-dashboard/admin-dashboard.component';
import { VuelosAdminComponent } from './pages/administrador/vuelos-admin/vuelos-admin.component';
import { EstadisticasAdminComponent } from './pages/administrador/estadisticas-admin/estadisticas-admin.component';
import { FlotaAereaAdminComponent } from './pages/administrador/flota-aerea-admin/flota-aerea-admin.component';
import { NominaAdminComponent } from './pages/administrador/roster/nomina-admin/nomina-admin.component';
import { ReportesAdminComponent } from './pages/administrador/reportes-admin/reportes-admin.component';
import { CreatePlane } from './pages/administrador/create-plane/create-plane';
import { NewEmployed } from './pages/administrador/new-employed/new-employed';
import { Personal } from './pages/administrador/personal/personal';
import { DetallesPersonal } from './pages/administrador/detalles-personal/detalles-personal';
import { MyProfile } from './shared/my-profile/my-profile';
import { VerificacionDosFactoresComponent } from './pages/auth/verificacionDos-factores/verificacion-dos-factores.component';
import { CreateRoster } from './pages/administrador/roster/create-roster/create-roster';
import { DetallesAeronave } from './pages/administrador/detalles-aeronave/detalles-aeronave';
import { NuevoTripulante } from './pages/administrador/nuevo-tripulante/nuevo-tripulante';
import { Usuarios } from './pages/administrador/usuarios/usuarios';
import { EditNomina } from './pages/administrador/roster/edit-nomina/edit-nomina';
import {AgendarVueloComponent} from './pages/agendar-vuelo/agendar-vuelo.component';
import { DetallesVuelo } from './pages/administrador/detalles-vuelo/detalles-vuelo';


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
      },
      {
        path: 'recuperar',
        component: RecuperarSolicitudComponent
      },
      {
        path: 'recuperar/verificar',
        component: RecuperarContraseniaComponent
      },
      {
        path: 'verificacion-2fa',
        component: VerificacionDosFactoresComponent
      }
    ]
  },
  {
    path: 'dashboard',
    component: ClientDashboardComponent,
  },
  {
    path: 'agendar-vuelo',
    component: AgendarVueloComponent,
    canActivate: [authGuard]
  },
  {
    path: 'boleta/:id',
    component: BoletaComponent,
    canActivate: [authGuard]
  },
  {
    path: 'vuelo/:id',
    redirectTo: 'boleta/:id' // opcional, para compatibilidad
  },
  {
    path: 'terminos-condiciones',
    component: TerminosCondicionesComponent
  },
  {
    path: 'politica-privacidad',
    component: PoliticaPrivacidadComponent
  },
  {
    path: 'trabaja-con-nosotros',
    component: TrabajaConNosotrosComponent
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
    path: 'tripulante-perfil',
    component: MyProfile,
    canActivate: []
  },
  {
    path: 'perfil',
    component: ProfileComponent
  },
  {
    path: 'admin',
    canActivate: [authGuard],
    children: [
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      },
      {
        path: 'my-profile',
        component: ProfileComponent
       },
      {
        path: 'dashboard',
        component: AdminDashboardComponent
      },
      {
        path: 'vuelos-admin',
        component: VuelosAdminComponent
      },
      {
        path: 'vuelo/:id',
        component: DetallesVuelo
      },
      {
        path: 'estadisticas',
        component: EstadisticasAdminComponent
      },
      {
        path: 'flota',
        component: FlotaAereaAdminComponent,
      },
      {
        path: 'flota/:id',
        component: DetallesAeronave
      },
      {
        path: 'create-plane',
        component: CreatePlane
      },
      {
        path: 'nomina',
        component: NominaAdminComponent,
      },
      {
        path: 'nomina/:id',
        component: EditNomina
       },
      {
       path: 'nuevo-empleado',
       component: NewEmployed,
      },
      {
        path: 'nuevo-tripulante',
        component: NuevoTripulante
      },
      {
        path: 'reportes',
        component: ReportesAdminComponent
      },
      {
        path: 'personal',
        component: Personal
      },
      {
        path: 'persona/:id',
        component: DetallesPersonal
      },
      {
        path: 'create-roster',
        component: CreateRoster
      },
      {
        path: 'usuarios',
        component: Usuarios
      }
    ]
  },
  {
    path: 'operador',
    children: [
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      },
      {
        path: 'dashboard',
        component: DashboardOperadorJComponent
      },
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
    path: 'sobre-nosotros',
    component: SobreNosotrosComponent
  },
  {
    path: 'flota',
    component: NuestraFlotaComponent
  }
];
