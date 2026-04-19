import { EstadoVuelo } from './estado-vuleo';

export interface updateVuelo {
  aeronaveId?: number;
  tripulacionIds?: number[];

  origen?: string;
  destino?: string;

  fechaSalidaProgramada?: string;   // LocalDateTime → string ISO
  fechaLlegadaProgramada?: string;

  fechaSalidaReal?: string;
  fechaLlegadaReal?: string;

  numeroPasajeros?: number;

  estado?: EstadoVuelo;

  proposito?: string;
  observaciones?: string;

  costoEstimado?: number;
}