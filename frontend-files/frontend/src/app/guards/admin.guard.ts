import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth/auth.service';
import { RolUsuario } from '../models/users/auth.models';

/**
 * Guard de ruta que verifica si el usuario tiene rol de ADMINISTRADOR.
 * Protege las rutas que solo deben ser accesibles por administradores.
 */
export const adminGuard: CanActivateFn = (route, state) => {
  // Inyectar dependencias necesarias
  const authService = inject(AuthService); // Servicio de autenticación
  const router = inject(Router); // Router para navegación

  // Obtener el usuario actual
  const currentUser = authService.currentUserValue;

  // Verificar si el usuario está autenticado y tiene rol de administrador
  if (authService.isAuthenticated() && currentUser?.rol === RolUsuario.ADMINISTRADOR) {
    return true; // Permite el acceso a la ruta
  }

  console.warn('Acceso denegado: Se requiere rol de ADMINISTRADOR');

  // Redirigir según corresponda
  if (authService.isAuthenticated()) {
    // Usuario autenticado pero sin permisos de administrador
    router.navigate(['/dashboard']);
  } else {
    // Usuario no autenticado
    router.navigate(['/auth/login']);
  }

  return false; // Deniega el acceso
};
