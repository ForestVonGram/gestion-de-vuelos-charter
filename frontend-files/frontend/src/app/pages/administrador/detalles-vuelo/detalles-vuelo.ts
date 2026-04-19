import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminSidebarComponent } from '../../../shared/admin-sidebar/admin-sidebar.component';
import { AccesibilidadComponent } from '../../../shared/accesibilidad/accesibilidad.component';
import { AuthService } from '../../../services/auth/auth.service';
import { VuelosService } from '../../../services/vuelos/vuelos.service';
import { FormGroup, ReactiveFormsModule, FormBuilder } from '@angular/forms';
import Swal from 'sweetalert2';
import { Aeronave } from '../../../services/vuelos/aeronave_service';
import { Tripulante } from '../../../services/personal/tripulante';
import { ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'app-detalles-vuelo',
  standalone: true,
  imports: [AdminSidebarComponent, AccesibilidadComponent, CommonModule, ReactiveFormsModule],
  templateUrl: './detalles-vuelo.html',
  styleUrl: './detalles-vuelo.css',
})
export class DetallesVuelo implements OnInit {

  currentUser: any;
  vuelo: any;

  formVuelo!: FormGroup;

  aeronaves: any[] = [];
  tripulacion: any[] = [];

  tripulacionSeleccionada: number[] = [];

  constructor(
    private authService: AuthService,
    private vuelosService: VuelosService,
    private fb: FormBuilder, private aeronave: Aeronave, private tripulantes: Tripulante,
    private cdr: ChangeDetectorRef
  ) {
    this.currentUser = this.authService.currentUserValue;
  }

  ngOnInit(): void {

    this.formVuelo = this.fb.group({
      estado: [''],
      origen: [''],
      destino: [''],
      fechaSalidaProgramada: [''],
      fechaLlegadaProgramada: [''],
      numeroPasajeros: [0],
      aeronaveId: [],
      tripulacionIds: [[]],
      proposito: [''],
      observaciones: [''],
      costoEstimado: [0]
    });

    this.cargarAeronaves();
    this.cargarTripulacion();
    this.cargarVuelo(Number(window.location.pathname.split('/').pop()));
  }

  cargarVuelo(id: number): void {
    this.vuelosService.obtenerVueloId(id).subscribe({
      next: (data) => {
        this.vuelo = data;
        this.formVuelo.patchValue({
          estado: data.estado,
          origen: data.origen,
          destino: data.destino,
          fechaSalidaProgramada: data.fechaSalidaProgramada,
          fechaLlegadaProgramada: data.fechaLlegadaProgramada,
          numeroPasajeros: data.numeroPasajeros,
          aeronaveId: data.aeronave?.id,
          tripulacionIds: data.tripulacion ? data.tripulacion.map((t: any) => t.id) : [],
          proposito: data.proposito,
          observaciones: data.observaciones,
          costoEstimado: data.costoEstimado
        });
      },
      error: (e) => console.error('Error cargando vuelo:', e)
    });
  }


  // =========================
  // CARGAR AERONAVES
  // =========================
  cargarAeronaves(): void {
    this.aeronave.getAeronaves().subscribe({
      next: (data) => {
        this.aeronaves = data;
      },
      error: (e) => console.error('Error aeronaves:', e)
    });
  }


  cargarTripulacion(): void {
    this.tripulantes.obtenerTripulantesNoPage().subscribe({
      next: (data) => {
        this.tripulacion = data.content; 
        this.cdr.detectChanges();
      },
      error: (e) => console.error('Error tripulación:', e)
    });
  }

  // =========================
  // SELECCIÓN MULTIPLE
  // =========================
  onTripulacionChange(event: any, id: number): void {

    const seleccionados = this.formVuelo.get('tripulacionIds')?.value || [];

    if (event.target.checked) {
      if (!seleccionados.includes(id)) {
        seleccionados.push(id);
      }
    } else {
      const index = seleccionados.indexOf(id);
      if (index > -1) {
        seleccionados.splice(index, 1);
      }
    }

    this.formVuelo.patchValue({
      tripulacionIds: seleccionados
    });
  }

  // =========================
  // GUARDAR
  // =========================
  guardarCambios(): void {

    if (!this.vuelo?.id) {
      Swal.fire('Error', 'No hay vuelo seleccionado', 'error');
      return;
    }
    const formValue = this.formVuelo.value;

    const payload = {
      ...formValue,
      aeronaveId: formValue.aeronaveId ? Number(formValue.aeronaveId) : null,
      tripulacionIds: formValue.tripulacionIds || []
    };

    console.log('Payload FINAL:', payload);

    this.vuelosService.actualizarVuelo(this.vuelo.id, payload).subscribe({
      next: () => {
        Swal.fire('Éxito', 'Vuelo actualizado correctamente', 'success');
      },
      error: (error) => {
        console.error(error);
        Swal.fire('Error', 'No se pudo actualizar el vuelo', 'error');
      }
    });
  }

  volver(): void {
    window.history.back();
  }
}