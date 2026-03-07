export interface CreateVueloDTO {
  usuarioId: number;
  origen: string;
  destino: string;
  fechaSalidaProgramada: string;
  fechaLlegadaProgramada: string;
  numeroPasajeros: number;
  proposito?: string;
  observaciones?: string;
}