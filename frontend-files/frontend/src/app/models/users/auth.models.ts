// Enums basados en tus dominios de Java
export enum RolUsuario {
  USUARIO = 'USUARIO',
  ADMINISTRADOR = 'ADMINISTRADOR',
  OPERADOR_LOGISTICA = 'OPERADOR_LOGISTICA',
  AYUDANTE_MANTENIMIENTO = 'AYUDANTE_MANTENIMIENTO',
  TRIPULACION = 'TRIPULACION'
  // Agrega otros roles si existen en tu backend
}

export enum MetodoDosFactores {
  EMAIL = 'EMAIL',
  SMS = 'SMS'
}

// --- DTOs de Petición (Requests) ---

export interface LoginRequest {
  email: string;
  password: string;
  recaptchaToken: string;
}

export interface RegisterRequest {
  nombre: string;
  apellido: string;
  email: string;
  password: string;
  telefono?: string; // Opcional porque no tiene @NotBlank en Java
  rol: RolUsuario;
  recaptchaToken: string;
}

export interface ForgotPasswordRequest {
  email: string;
}

export interface ResetPasswordRequest {
  token: string;
  nuevaPassword: string;
}

export interface ChangePasswordRequest {
  passwordActual: string;
  nuevaPassword: string;
}

export interface VerificacionCodigoRequest {
  codigo: string; // El código de 6 dígitos
}

export interface ConfiguracionDosFactoresDTO {
  habilitado: boolean;
  metodo?: MetodoDosFactores;
  destino?: string; // Opcional (si es null usa el del usuario)
}

// --- DTOs de Respuesta (Responses) ---

export interface AuthResponse {
  token: string;
  tokenType: string;
  userId: number;
  email: string;
  nombreCompleto: string;
  rol: RolUsuario;
  requires2FA: boolean;
  sessionToken?: string; // Token temporal para el flujo de 2FA
}

export interface Verificacion2FAResponse {
  metodo: MetodoDosFactores;
  destino: string; // El destino enmascarado (ej: f***@gmail.com)
  tiempoExpiracion: number;
  intentosRestantes: number;
}

export interface EstadoDosFactoresDTO {
  habilitado: boolean;
  metodo?: MetodoDosFactores;
  destino?: string; // Enmascarado
}

export interface SesionActivaDTO {
  id: number;
  dispositivo: string;
  direccionIp: string;
  fechaCreacion: string; // Las fechas vienen como string ISO desde Java
  fechaExpiracion: string;
  ultimaActividad: string;
  sesionActual: boolean;
}

export interface GoogleAuthRequest {
  credential: string; // ID Token emitido por Google Identity Services
}
