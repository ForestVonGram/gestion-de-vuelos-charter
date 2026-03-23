import { CargoPersonal } from './cargo';
import { EstadoPersonal } from './estado-personal';

export interface PersonalUpdateDTO {
  cargo: CargoPersonal;
  estado: EstadoPersonal;
  areaEspecializacion: string;
  certificaciones: string;
  fechaContratacion: string; // ISO date: 'YYYY-MM-DD'
  turno: string;
  observaciones: string;
}
