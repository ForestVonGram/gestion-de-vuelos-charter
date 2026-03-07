import { EstadoAeronave } from "./estado-avion";
import { ImagenAeronaveDTO } from "./imagen-aeronave-dto";

export interface AeronaveDTO {
  id: number;
  matricula: string;
  modelo: string;
  fabricante: string;
  capacidadPasajeros: number;
  capacidadTripulacion: number;
  autonomiaKm: number;
  velocidadCruceroKmh: number;
  fechaFabricacion: string;
  fechaUltimaRevision: string;
  horasVueloTotales: number;
  estado: EstadoAeronave;
  especificacionesTecnicas: string;
  imagenes: ImagenAeronaveDTO[];
}