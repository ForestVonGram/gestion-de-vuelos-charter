import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'truncate',
  standalone: true // Definido como standalone para importarlo directamente en componentes
})
export class TruncatePipe implements PipeTransform {
  /**
   * Transforma un texto largo en una versión acortada.
   * @param value El texto original a procesar.
   * @param limit Cantidad máxima de caracteres permitidos (por defecto 50).
   * @param trail Caracteres a añadir al final si se corta el texto (por defecto '...').
   */
  transform(value: string, limit: number = 50, trail: string = '...'): string {
    // Si el valor es nulo o indefinido, retorna una cadena vacía
    if (!value) return '';

    // Si la longitud supera el límite, corta el texto y concatena el rastro (trail)
    // De lo contrario, devuelve el texto original intacto
    return value.length > limit ? value.substring(0, limit) + trail : value;
  }
}
