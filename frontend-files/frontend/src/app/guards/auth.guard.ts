import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth/auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // 1. Preguntamos al AuthService si está autenticado
  if (authService.isAuthenticated()) {
    return true; // ¡Pase usted!
  } else {
    // 2. Si no, lo mandamos al login
    router.navigate(['/auth/login']);
    return false; // Acceso denegado
  }
};
