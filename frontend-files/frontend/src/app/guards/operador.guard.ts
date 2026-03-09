import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth/auth.service';
import { RolUsuario } from '../models/users/auth.models';

/**
 * Guardián funcional para proteger rutas restringidas a Operadores de Logística o Administradores.
 * Se utiliza en la configuración de rutas (app.routes.ts) mediante la propiedad 'canActivate'.
 */
export const operadorGuard: CanActivateFn = (route, state) => {
  // Inyección de servicios necesarios dentro del contexto funcional
  const authService = inject(AuthService);
  const router = inject(Router);

  // Obtener el estado actual del usuario desde el observable/BehaviorSubject del servicio
  const currentUser = authService.currentUserValue;

  /**
   * Lógica de Validación:
   * 1. Comprueba si el token es válido y no ha expirado.
   * 2. Verifica si el rol asignado coincide con OPERADOR_LOGISTICA o el super-rol ADMINISTRADOR.
   */
  if (authService.isAuthenticated() &&
    (currentUser?.rol === RolUsuario.OPERADOR_LOGISTICA || currentUser?.rol === RolUsuario.ADMINISTRADOR)) {
    // Si cumple los requisitos, permite la navegación al componente solicitado
    return true;
  }

  // Registro de advertencia en consola para depuración en desarrollo
  console.warn('Acceso denegado: Se requiere rol de OPERADOR o ADMINISTRADOR');

  /**
   * Lógica de Redirección:
   * - Si el usuario ya está logueado pero no tiene el rol, lo envía al Dashboard general.
   * - Si ni siquiera está autenticado, lo envía a la pantalla de Login.
   */
  if (authService.isAuthenticated()) {
    router.navigate(['/dashboard']);
  } else {
    router.navigate(['/auth/login']);
  }

  // Deniega el acceso a la ruta original solicitada
  return false;
};
