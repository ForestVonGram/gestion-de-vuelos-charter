import { TipoImagenAeronave } from './tipo-imagen-aeronave';    

export interface ImagenAeronaveDTO {
  id: number;
  urlImagen: string;
  idCloudinary: string;
  tipo: TipoImagenAeronave;
  descripcion: string;
  ordenVisualizacion: number;
  fechaCarga: string;
  tamanoBytes: number;
  cargadoPorNombre: string;
}