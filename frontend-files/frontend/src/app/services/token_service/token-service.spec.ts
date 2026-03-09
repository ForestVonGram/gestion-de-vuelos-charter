import { TestBed } from '@angular/core/testing';
import { TokenService} from './token-service';

describe('TokenService', () => {
  let service: TokenService;

  // Configuración previa a cada prueba (Test Suite Setup)
  beforeEach(() => {
    // TestBed crea un entorno de módulo virtual para probar el servicio de forma aislada
    TestBed.configureTestingModule({});
    // Inyectamos la instancia del servicio dentro de la variable local para usarla en los tests
    service = TestBed.inject(TokenService);
  });

  /**
   * Prueba de humo (Smoke Test):
   * Verifica que el servicio se instancie correctamente por el inyector de dependencias de Angular.
   */
  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
