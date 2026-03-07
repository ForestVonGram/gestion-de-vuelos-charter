import { CargoPersonal } from "./cargo";

export interface PersonalCreateDTO {

  usuarioId: number;

  numeroEmpleado: string;

  cargo: CargoPersonal;

  areaEspecializacion?: string;

  certificaciones?: string;

  fechaContratacion?: string; // LocalDate en Java → string en formato YYYY-MM-DD

  turno?: string;

  observaciones?: string;

}