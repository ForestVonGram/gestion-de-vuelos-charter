export interface ProfileDto {
    id: number;
    usuarioId: number;
    usuarioNombre: string;
    usuarioApellido: string;
    usuarioEmail: string;
    numeroLicencia: string;
    tipoLicencia: string;
    fechaExpedicionLicencia: Date;
    fechaVencimientoLicencia: Date;
    horasVueloTotales: number;
    horasVueloMes: number;
    estado: string;
    esPiloto: boolean;
    certificaciones: string[];
    observaciones: string;
}
