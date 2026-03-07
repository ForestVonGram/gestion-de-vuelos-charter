import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth/auth.service';
import { RolUsuario } from '../models/users/auth.models';

export const operadorGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const currentUser = authService.currentUserValue;

  // Verificamos usando el Enum que ya tienes
  if (authService.isAuthenticated() &&
    (currentUser?.rol === RolUsuario.OPERADOR_LOGISTICA || currentUser?.rol === RolUsuario.ADMINISTRADOR)) {
    return true;
  }

  console.warn('Acceso denegado: Se requiere rol de OPERADOR o ADMINISTRADOR');

  if (authService.isAuthenticated()) {
    router.navigate(['/dashboard']);
  } else {
    router.navigate(['/auth/login']);
  }

  return false;
};
