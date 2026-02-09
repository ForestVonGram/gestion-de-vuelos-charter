# Funcionalidad de Imágenes de Aeronaves

Este documento describe la nueva funcionalidad de carga y gestión de imágenes para aeronaves integrada con Cloudinary.

## Overview

Los usuarios pueden ahora:
- Subir imágenes de aeronaves (exterior, interior, cabina, baño, equipamiento, etc.)
- Visualizar imágenes clasificadas por tipo
- Eliminar imágenes
- Reordenar imágenes
- Ver imágenes al consultar datos de aeronaves

## Configuración de Cloudinary

### Requisitos previos

1. Crear una cuenta en [Cloudinary](https://cloudinary.com/)
2. Obtener las credenciales:
   - **Cloud Name**
   - **API Key**
   - **API Secret**

### Variables de Entorno

Configura las siguientes variables de entorno en tu servidor o archivo `.env`:

```bash
CLOUDINARY_CLOUD_NAME=tu_cloud_name
CLOUDINARY_API_KEY=tu_api_key
CLOUDINARY_API_SECRET=tu_api_secret
```

### Archivo de Propiedades

En `backend/src/main/resources/application.properties`:

```properties
cloudinary.cloud-name=${CLOUDINARY_CLOUD_NAME}
cloudinary.api-key=${CLOUDINARY_API_KEY}
cloudinary.api-secret=${CLOUDINARY_API_SECRET}
```

## Estructura de Datos

### Entidad: ImagenAeronave

```
- id (Long) - ID único
- aeronave (Aeronave) - Referencia a la aeronave
- urlImagen (String) - URL de la imagen en Cloudinary
- idCloudinary (String) - ID público de Cloudinary
- tipo (TipoImagenAeronave) - EXTERIOR, INTERIOR, CABINA, COCKPIT, BANO, COCINA, EQUIPAMIENTO, OTRO
- descripcion (String) - Descripción opcional de la imagen
- ordenVisualizacion (Integer) - Orden en la galería
- fechaCarga (LocalDateTime) - Fecha de carga
- tamanoBytes (Long) - Tamaño del archivo
- cargadoPor (Personal) - Usuario que cargó la imagen
```

### Enum: TipoImagenAeronave

```java
EXTERIOR       // Vistas exteriores del avión
INTERIOR       // Vistas generales del interior
CABINA         // Cabina de pasajeros, asientos
COCKPIT        // Cabina de pilotos
BANO           // Baño
COCINA         // Área de cocina/catering
EQUIPAMIENTO   // Equipamiento especial
OTRO           // Otros tipos de imágenes
```

## API Endpoints

### Cargar una imagen

**POST** `/api/aeronaves/{aeronaveId}/imagenes`

**Parámetros:**
- `aeronaveId` (path) - ID de la aeronave
- `file` (multipart) - Archivo de imagen
- `tipo` (query) - Tipo de imagen (EXTERIOR, INTERIOR, CABINA, etc.)
- `descripcion` (query, opcional) - Descripción de la imagen
- `orden` (query, opcional) - Orden de visualización

**Ejemplo:**
```bash
curl -X POST "http://localhost:8080/api/aeronaves/1/imagenes" \
  -F "file=@imagen.jpg" \
  -F "tipo=EXTERIOR" \
  -F "descripcion=Vista frontal" \
  -F "orden=1"
```

**Respuesta (201 Created):**
```json
{
  "id": 1,
  "urlImagen": "https://res.cloudinary.com/...",
  "idCloudinary": "aeronaves/EC-123/imagen_123456",
  "tipo": "EXTERIOR",
  "descripcion": "Vista frontal",
  "ordenVisualizacion": 1,
  "fechaCarga": "2026-02-08T21:50:00",
  "tamanoBytes": 2048576,
  "cargadoPorNombre": "Juan Pérez"
}
```

### Cargar múltiples imágenes

**POST** `/api/aeronaves/{aeronaveId}/imagenes-multiples`

**Parámetros:**
- `aeronaveId` (path) - ID de la aeronave
- `files` (multipart) - Lista de archivos de imagen
- `tipo` (query) - Tipo de imagen para todas
- `descripcion` (query, opcional) - Descripción para todas

### Obtener imágenes de una aeronave

**GET** `/api/aeronaves/{aeronaveId}/imagenes`

**Parámetros:**
- `aeronaveId` (path) - ID de la aeronave

**Respuesta (200 OK):**
```json
[
  {
    "id": 1,
    "urlImagen": "https://res.cloudinary.com/...",
    "tipo": "EXTERIOR",
    "ordenVisualizacion": 0,
    "fechaCarga": "2026-02-08T21:50:00"
  },
  {
    "id": 2,
    "urlImagen": "https://res.cloudinary.com/...",
    "tipo": "INTERIOR",
    "ordenVisualizacion": 1,
    "fechaCarga": "2026-02-08T21:51:00"
  }
]
```

### Filtrar imágenes por tipo

**GET** `/api/aeronaves/{aeronaveId}/imagenes/tipo/{tipo}`

**Parámetros:**
- `aeronaveId` (path) - ID de la aeronave
- `tipo` (path) - Tipo de imagen (EXTERIOR, INTERIOR, CABINA, etc.)

### Obtener una imagen específica

**GET** `/api/aeronaves/imagenes/{imagenId}`

**Parámetros:**
- `imagenId` (path) - ID de la imagen

### Eliminar una imagen

**DELETE** `/api/aeronaves/{aeronaveId}/imagenes/{imagenId}`

**Parámetros:**
- `aeronaveId` (path) - ID de la aeronave
- `imagenId` (path) - ID de la imagen

**Respuesta:** 204 No Content

### Actualizar orden de imágenes

**PUT** `/api/aeronaves/{aeronaveId}/imagenes/orden`

**Body (JSON):**
```json
{
  "1": 0,
  "2": 1,
  "3": 2
}
```

Donde la clave es el ID de la imagen y el valor es el nuevo orden.

### Reordenar imágenes automáticamente

**POST** `/api/aeronaves/{aeronaveId}/imagenes/reordenar`

Reordena las imágenes basado en su fecha de carga (más antiguas primero).

**Respuesta:** 204 No Content

## Integración con Aeronave

Cuando se recuperan datos de una aeronave con `GET /api/aeronaves/{id}`, ahora se incluye la lista de imágenes:

**GET** `/api/aeronaves/1`

**Respuesta (200 OK):**
```json
{
  "id": 1,
  "matricula": "EC-123",
  "modelo": "Boeing 737",
  "fabricante": "Boeing",
  "capacidadPasajeros": 180,
  "capacidadTripulacion": 6,
  "autonomiaKm": 5000,
  "velocidadCruceroKmh": 500,
  "estado": "DISPONIBLE",
  "especificacionesTecnicas": "...",
  "imagenes": [
    {
      "id": 1,
      "urlImagen": "https://res.cloudinary.com/...",
      "tipo": "EXTERIOR",
      "descripcion": "Vista frontal",
      "ordenVisualizacion": 0,
      "fechaCarga": "2026-02-08T21:50:00",
      "tamanoBytes": 2048576
    }
  ]
}
```

## Comportamiento Especial

### Almacenamiento de imágenes

Las imágenes se organizan en Cloudinary en carpetas por matrícula de aeronave:

```
aeronaves/
├── EC-123/
│   ├── imagen1_1707432600000
│   ├── imagen2_1707432700000
│   └── imagen3_1707432800000
├── EC-456/
│   └── imagen1_1707432600000
```

### Eliminación en cascada

Cuando se elimina una aeronave, todas sus imágenes se eliminan automáticamente tanto de la base de datos como de Cloudinary.

### Orden de visualización

Las imágenes se devuelven ordenadas automáticamente por `ordenVisualizacion`:
- El rango es 0 a N (flexible)
- Valores duplicados se manejan alfabéticamente por fecha

## Validaciones

- El archivo debe ser una imagen válida (MIME type comienza con `image/`)
- El tipo de imagen debe ser un valor válido del enum `TipoImagenAeronave`
- La aeronave debe existir
- Solo se puede eliminar una imagen si pertenece a la aeronave especificada

## Manejo de Errores

### 400 Bad Request
- Archivo vacío o inválido
- Tipo de imagen no válido

### 404 Not Found
- Aeronave no encontrada
- Imagen no encontrada

### 500 Internal Server Error
- Error al conectar con Cloudinary
- Error al guardar en la base de datos

## Ejemplos de Uso

### Con cURL

```bash
# Subir imagen
curl -X POST "http://localhost:8080/api/aeronaves/1/imagenes" \
  -H "Authorization: Bearer TOKEN" \
  -F "file=@exterior.jpg" \
  -F "tipo=EXTERIOR" \
  -F "descripcion=Vista principal del fuselaje"

# Obtener imágenes de la aeronave
curl -X GET "http://localhost:8080/api/aeronaves/1/imagenes" \
  -H "Authorization: Bearer TOKEN"

# Obtener imágenes por tipo
curl -X GET "http://localhost:8080/api/aeronaves/1/imagenes/tipo/INTERIOR" \
  -H "Authorization: Bearer TOKEN"

# Eliminar imagen
curl -X DELETE "http://localhost:8080/api/aeronaves/1/imagenes/5" \
  -H "Authorization: Bearer TOKEN"
```

### Con Angular/TypeScript

```typescript
// Servicio de aeronaves
cargarImagenAeronave(aeronaveId: number, file: File, tipo: string): Observable<ImagenAeronaveDTO> {
  const formData = new FormData();
  formData.append('file', file);
  formData.append('tipo', tipo);
  
  return this.http.post<ImagenAeronaveDTO>(
    `/api/aeronaves/${aeronaveId}/imagenes`,
    formData
  );
}

obtenerImagenesAeronave(aeronaveId: number): Observable<ImagenAeronaveDTO[]> {
  return this.http.get<ImagenAeronaveDTO[]>(
    `/api/aeronaves/${aeronaveId}/imagenes`
  );
}

eliminarImagenAeronave(aeronaveId: number, imagenId: number): Observable<void> {
  return this.http.delete<void>(
    `/api/aeronaves/${aeronaveId}/imagenes/${imagenId}`
  );
}
```

## Testing

Para probar localmente sin Cloudinary real, puedes usar las variables de entorno de prueba:

```bash
export CLOUDINARY_CLOUD_NAME=demo
export CLOUDINARY_API_KEY=123456789
export CLOUDINARY_API_SECRET=test_secret
```

**Nota:** En producción, siempre usa credenciales reales.

## Troubleshooting

### Las imágenes no se cargan
- Verifica que las credenciales de Cloudinary sean correctas
- Revisa los logs del servidor para errores de conexión
- Asegúrate de que el archivo sea una imagen válida

### Las imágenes no aparecen en la respuesta GET
- Verifica que la relación OneToMany esté correctamente configurada
- Revisa que las imágenes estén asociadas a la aeronave correcta

### Error "Invalid enum value"
- Verifica que el tipo de imagen sea un valor válido: EXTERIOR, INTERIOR, CABINA, COCKPIT, BANO, COCINA, EQUIPAMIENTO, OTRO

## Cambios a la Base de Datos

Se creó la tabla `imagenes_aeronaves` con las siguientes columnas:

```sql
CREATE TABLE imagenes_aeronaves (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    aeronave_id BIGINT NOT NULL,
    url_imagen TEXT NOT NULL,
    id_cloudinary VARCHAR(255) NOT NULL UNIQUE,
    tipo VARCHAR(50) NOT NULL,
    descripcion TEXT,
    orden_visualizacion INT,
    fecha_carga TIMESTAMP NOT NULL,
    tamaño_bytes BIGINT,
    cargado_por_id BIGINT,
    FOREIGN KEY (aeronave_id) REFERENCES aeronaves(id),
    FOREIGN KEY (cargado_por_id) REFERENCES personal(id)
);
```
