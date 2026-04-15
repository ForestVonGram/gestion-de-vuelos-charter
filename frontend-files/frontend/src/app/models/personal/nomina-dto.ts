import { EstadoNomina } from './estado-nomina';
export interface NominaDTO {
  id: number;
  personalId: number;
  personalNombre: string;
  personalApellido: string;
  mes: number;
  ano: number;
  salarioBase: number;
  deducciones: number;
  bonificaciones: number;
  descuentoImpuesto: number;
  descuentoAfiliacion: number;
  totalNeto: number;
  estado: EstadoNomina;
  fechaPago: string; // ISO 8601
  fechaGeneracion: string; // ISO 8601
  observaciones: string;
}