import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth/auth.service';

// Guard que protege rutas y solo permite acceso a usuarios autenticados
export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService); // Inyección del servicio de autenticación
  const router = inject(Router); // Inyección del router para redirecciones

  // 1. Preguntamos al AuthService si está autenticado
  if (authService.isAuthenticated()) {
    return true; // ¡Pase usted!
  } else {
    // 2. Si no, lo mandamos al login
    router.navigate(['/auth/login']);
    return false; // Acceso denegado
  }
};
