import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth/auth.service';
import { RolUsuario } from '../models/auth.models';

export const adminGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const currentUser = authService.currentUserValue;

  // Verificamos usando el Enum que ya tienes
  if (authService.isAuthenticated() && currentUser?.rol === RolUsuario.ADMINISTRADOR) {
    return true;
  }

  console.warn('Acceso denegado: Se requiere rol de ADMINISTRADOR');

  if (authService.isAuthenticated()) {
    router.navigate(['/dashboard']);
  } else {
    router.navigate(['/auth/login']);
  }

  return false;
};
