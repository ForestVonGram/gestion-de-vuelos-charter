export interface CreateTripulanteDto {
  usuarioId: number;
  numeroLicencia: string;
  tipoLicencia?: string;
  fechaExpedicionLicencia?: string; // ISO date (YYYY-MM-DD)
  fechaVencimientoLicencia?: string; // ISO date (YYYY-MM-DD)
  esPiloto?: boolean;
  certificaciones?: string;
  observaciones?: string;
}