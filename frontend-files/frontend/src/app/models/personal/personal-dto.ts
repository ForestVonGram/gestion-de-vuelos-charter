import { CargoPersonal } from "./cargo";
import { EstadoPersonal } from "./estado-personal";

export interface PersonalDTO {
  id: number;
  usuarioId: number;
  usuarioNombre: string;
  usuarioEmail: string;
  numeroEmpleado: string;
  cargo: CargoPersonal;
  estado: EstadoPersonal;
  areaEspecializacion: string;
  certificaciones: string;
  fechaContratacion: string; // ISO date string: 'YYYY-MM-DD'
  turno: string;
  observaciones: string;
}