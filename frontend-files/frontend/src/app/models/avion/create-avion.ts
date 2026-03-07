import { EstadoAeronave } from "./estado-avion";
export interface createAvion {
  matricula: string;
  modelo: string;
  fabricante?: string;
  capacidadPasajeros?: number;
  capacidadTripulacion?: number;
  autonomiaKm?: number;
  velocidadCruceroKmh?: number;
  fechaFabricacion?: string;
  fechaUltimaRevision?: string;
  estado?: EstadoAeronave;
  especificacionesTecnicas?: string;
}