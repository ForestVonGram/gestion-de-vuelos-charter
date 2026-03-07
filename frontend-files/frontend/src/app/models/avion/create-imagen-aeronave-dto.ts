import { TipoImagenAeronave } from "./tipo-imagen-aeronave";

export interface ImagenAeronaveCreateDTO {
  tipo: TipoImagenAeronave;
  descripcion?: string;
  ordenVisualizacion?: number;
}
