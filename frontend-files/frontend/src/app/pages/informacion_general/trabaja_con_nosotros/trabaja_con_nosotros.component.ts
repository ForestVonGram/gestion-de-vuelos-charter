import { Component, OnInit, OnDestroy, HostListener, Renderer2 } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-trabaja-con-nosotros',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule],
  templateUrl: './trabaja_con_nosotros.component.html',
  styleUrls: ['./trabaja_con_nosotros.component.css']
})
export class TrabajaConNosotrosComponent implements OnInit, OnDestroy {
  isNavbarScrolled = false;
  isDarkMode = false;
  applicationForm!: FormGroup;
  selectedFile: File | null = null;
  fileName: string = '';
  isSubmitting = false;
  submitSuccess = false;

  // Lista de cargos disponibles
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
    this.initForm();

    // Check for saved theme preference
    const savedTheme = localStorage.getItem('theme');
    if (savedTheme === 'dark') {
      this.isDarkMode = true;
      this.enableDarkMode();
    }
  }

  ngOnDestroy(): void {
    // Cleanup
  }

  @HostListener('window:scroll')
  onWindowScroll(): void {
    this.checkScroll();
  }

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

  private checkScroll(): void {
    const heroElement = document.querySelector('.careers-hero');
    if (heroElement) {
      const heroHeight = heroElement.clientHeight;
      this.isNavbarScrolled = window.scrollY > heroHeight - 100;
    } else {
      this.isNavbarScrolled = window.scrollY > 100;
    }
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      // Validar tipo de archivo (PDF, DOC, DOCX)
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

  onSubmit(): void {
    if (this.applicationForm.invalid) {
      Object.keys(this.applicationForm.controls).forEach(key => {
        const control = this.applicationForm.get(key);
        control?.markAsTouched();
      });
      return;
    }

    if (!this.selectedFile) {
      alert('Por favor, adjunta tu hoja de vida');
      return;
    }

    this.isSubmitting = true;

    // Aquí iría la lógica para enviar el formulario y el archivo al backend
    // Simulamos un envío exitoso
    setTimeout(() => {
      this.isSubmitting = false;
      this.submitSuccess = true;

      // Resetear formulario después de 3 segundos
      setTimeout(() => {
        this.submitSuccess = false;
        this.applicationForm.reset();
        this.selectedFile = null;
        this.fileName = '';
      }, 3000);
    }, 2000);
  }

  // Getters para facilitar el acceso en el HTML
  get f() { return this.applicationForm.controls; }
}
