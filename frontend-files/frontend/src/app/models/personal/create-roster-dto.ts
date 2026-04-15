export interface CreateRosterDTO {
  personalId: number;
  mes: number; // 1 - 12
  ano: number; // >= 2000
  salarioBase: number;

  deducciones?: number;        // default: 0.0
  bonificaciones?: number;     // default: 0.0
  descuentoImpuesto?: number;  // default: 0.0
  descuentoAfiliacion?: number;// default: 0.0

  observaciones?: string;
}
