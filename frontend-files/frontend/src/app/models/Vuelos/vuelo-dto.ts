import { EstadoVuelo } from "./estado-vuleo";

export interface VueloDTO {
  id: number;
  usuarioId: number;
  usuarioNombre: string;
  aeronaveId: number;
  aeronaveMatricula: string;
  tripulacionIds: number[];
  origen: string;
  destino: string;
  fechaSalidaProgramada: string;
  fechaLlegadaProgramada: string;
  fechaSalidaReal: string;
  fechaLlegadaReal: string;
  numeroPasajeros: number;
  estado: EstadoVuelo;
  proposito: string;
  observaciones: string;
  fechaSolicitud: string;
  costoEstimado: number;
}