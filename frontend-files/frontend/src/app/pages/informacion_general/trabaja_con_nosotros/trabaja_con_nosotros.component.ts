import { Component, OnInit, OnDestroy, HostListener, Renderer2 } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { AccesibilidadComponent} from '../../../shared/accesibilidad/accesibilidad.component';

@Component({
  selector: 'app-trabaja-con-nosotros',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule, AccesibilidadComponent],
  templateUrl: './trabaja_con_nosotros.component.html',
  styleUrls: ['./trabaja_con_nosotros.component.css']
})
export class TrabajaConNosotrosComponent implements OnInit, OnDestroy {
  // --- Estados de la interfaz y el formulario ---
  isNavbarScrolled = false;
  isDarkMode = false;
  applicationForm!: FormGroup; // Formulario reactivo para la postulación
  selectedFile: File | null = null; // Almacena el archivo (CV) seleccionado
  fileName: string = ''; // Nombre del archivo para mostrar en la UI
  isSubmitting = false; // Estado de carga durante el envío
  submitSuccess = false; // Estado de éxito tras el envío

  // Listado de vacantes disponibles para el selector del formulario
  positions = [
    'Piloto Comercial',
    'Copiloto',
    'Ingeniero de Mantenimiento',
    'Tripulante de Cabina (TCP)',
    'Personal de Tierra',
    'Agente de Servicio al Cliente',
    'Administrativo',
    'Técnico Aeronáutico',
    'Despachador de Vuelo',
    'Otro'
  ];

  constructor(
    private renderer: Renderer2,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.checkScroll();
    this.initForm(); // Inicializa el formulario al cargar

    // Restaura la preferencia de tema guardada
    const savedTheme = localStorage.getItem('theme');
    if (savedTheme === 'dark') {
      this.isDarkMode = true;
      this.enableDarkMode();
    }
  }

  ngOnDestroy(): void {
    // Limpieza de suscripciones o eventos si fuera necesario
  }

  // Detecta el scroll para cambiar el estilo de la Navbar
  @HostListener('window:scroll')
  onWindowScroll(): void {
    this.checkScroll();
  }

  // Configura los campos del formulario con sus respectivas validaciones
  private initForm(): void {
    this.applicationForm = this.fb.group({
      nombre: ['', [Validators.required, Validators.minLength(2)]],
      apellidos: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      telefono: ['', [Validators.required, Validators.pattern(/^[0-9]{7,15}$/)]],
      ciudad: ['', Validators.required],
      posicion: ['', Validators.required],
      experiencia: ['', Validators.required],
      visaTrabajo: [false],
      licenciaVigente: [false],
      disponibilidadInmediata: [false],
      comentarios: ['']
    });
  }

  // --- Lógica de Modo Oscuro ---
  toggleDarkMode(): void {
    this.isDarkMode = !this.isDarkMode;
    if (this.isDarkMode) {
      this.enableDarkMode();
      localStorage.setItem('theme', 'dark');
    } else {
      this.disableDarkMode();
      localStorage.setItem('theme', 'light');
    }
  }

  private enableDarkMode(): void {
    this.renderer.addClass(document.body, 'dark-theme');
    this.renderer.addClass(document.documentElement, 'dark-theme-active');
  }

  private disableDarkMode(): void {
    this.renderer.removeClass(document.body, 'dark-theme');
    this.renderer.removeClass(document.documentElement, 'dark-theme-active');
  }

  // Calcula el cambio de estilo de la Navbar según la altura del Hero
  private checkScroll(): void {
    const heroElement = document.querySelector('.careers-hero');
    if (heroElement) {
      const heroHeight = heroElement.clientHeight;
      this.isNavbarScrolled = window.scrollY > heroHeight - 100;
    } else {
      this.isNavbarScrolled = window.scrollY > 100;
    }
  }

  // Maneja la selección del archivo adjunto (Hoja de Vida) y valida su extensión
  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      const validTypes = ['application/pdf', 'application/msword', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'];
      if (validTypes.includes(file.type)) {
        this.selectedFile = file;
        this.fileName = file.name;
      } else {
        alert('Por favor, selecciona un archivo PDF o DOC/DOCX');
        event.target.value = '';
      }
    }
  }

  // Procesa el envío de la postulación
  onSubmit(): void {
    // Si el formulario es inválido, marca todos los campos para mostrar errores
    if (this.applicationForm.invalid) {
      Object.keys(this.applicationForm.controls).forEach(key => {
        const control = this.applicationForm.get(key);
        control?.markAsTouched();
      });
      return;
    }

    // Validación obligatoria del archivo adjunto
    if (!this.selectedFile) {
      alert('Por favor, adjunta tu hoja de vida');
      return;
    }

    this.isSubmitting = true;

    // Simulación de petición al servidor con delay
    setTimeout(() => {
      this.isSubmitting = false;
      this.submitSuccess = true;

      // Limpia el formulario automáticamente tras el éxito
      setTimeout(() => {
        this.submitSuccess = false;
        this.applicationForm.reset();
        this.selectedFile = null;
        this.fileName = '';
      }, 3000);
    }, 2000);
  }

  // Getter para simplificar el acceso a errores en el template HTML
  get f() { return this.applicationForm.controls; }
}
