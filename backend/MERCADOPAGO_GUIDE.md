# Guía de Implementación: Módulo de Mercado Pago

## 📋 Descripción General

Este documento describe la implementación completa del módulo de pagos integrado con Mercado Pago para el Sistema de Gestión de Vuelos Chárter. El módulo permite a los clientes realizar pagos parciales o completos para reservas de vuelos.

## 🏗️ Arquitectura

### Componentes Principales

```
backend/src/main/java/com/paeldav/backend/
├── domain/
│   ├── entity/Pago.java          # Entidad de pago
│   └── enums/EstadoPago.java     # Estados: PENDIENTE, CONFIRMADO, RECHAZADO, REEMBOLSADO
├── application/
│   ├── dto/pago/
│   │   ├── PagoDTO.java           # DTO de respuesta
│   │   ├── PagoCreateDTO.java     # DTO de creación
│   │   ├── ReembolsoDTO.java      # DTO para reembolsos
│   │   ├── PaymentWebhookDTO.java # DTO para webhooks detallados
│   │   └── ReembolsoResponseDTO.java
│   ├── mapper/PagoMapper.java     # MapStruct mapper
│   ├── service/
│   │   ├── base/PagoService.java  # Interfaz del servicio
│   │   ├── impl/PagoServiceImpl.java # Implementación
│   │   └── integration/MercadoPagoService.java # Integración con API
│   └── controller/PagoController.java # REST endpoints
└── infraestructure/
    └── repository/PagoRepository.java # Acceso a datos

backend/src/test/
├── java/com/paeldav/backend/
│   ├── application/service/
│   │   ├── integration/MercadoPagoServiceTest.java
│   │   └── impl/PagoServiceImplTest.java
│   ├── presentation/controller/PagoControllerTest.java
│   └── integration/PagoIntegrationTest.java
└── resources/application-test.properties
```

## 🔧 Configuración

### Variables de Entorno Requeridas

```bash
# Obligatorio para producción
export MERCADOPAGO_TOKEN="YOUR_MERCADO_PAGO_ACCESS_TOKEN"
export DB_URL="jdbc:postgresql://localhost:5432/charter_db"
export DB_USER="charter_user"
export DB_PASSWORD="secure_password"

# Webhook URL (solo para producción con Ngrok o reverse proxy)
export MERCADOPAGO_WEBHOOK_URL="https://your-domain.com/api/pagos/webhook"

# URLs de retorno del frontend
export FRONTEND_URL="https://your-frontend-domain.com"
```

### Configuración Local (application.properties)

```properties
mercadopago.token=${MERCADOPAGO_TOKEN}
mercadopago.success-url=${FRONTEND_URL}/pagos/exito
mercadopago.pending-url=${FRONTEND_URL}/pagos/pendiente
mercadopago.failure-url=${FRONTEND_URL}/pagos/error
mercadopago.webhook-url=${MERCADOPAGO_WEBHOOK_URL}
```

## 🚀 Endpoints REST

### Crear Pago
```http
POST /api/pagos
Content-Type: application/json

{
  "vueloId": 1,
  "usuarioId": 1,
  "monto": 100000.0,
  "emailCliente": "cliente@example.com",
  "descripcion": "Pago vuelo Bogotá-Cartagena"
}

Response (201 Created):
{
  "id": 1,
  "vueloId": 1,
  "usuarioId": 1,
  "monto": 100000.0,
  "estado": "PENDIENTE",
  "urlPago": "https://mercadopago.com/checkout/...",
  "numeroPreferencia": "12345678901",
  "emailCliente": "cliente@example.com"
}
```

### Obtener Pago por ID
```http
GET /api/pagos/{id}

Response (200 OK):
{
  "id": 1,
  "vueloId": 1,
  "usuarioId": 1,
  "usuarioNombre": "John Doe",
  "monto": 100000.0,
  "estado": "CONFIRMADO",
  "referenciaMercadoPago": "mp_123456789"
}
```

### Obtener Pagos por Vuelo
```http
GET /api/pagos/vuelo/{vueloId}

Response (200 OK):
[
  { ... pago 1 ... },
  { ... pago 2 ... }
]
```

### Obtener Pagos por Usuario
```http
GET /api/pagos/usuario/{usuarioId}

Response (200 OK):
[
  { ... pago 1 ... },
  { ... pago 2 ... }
]
```

### Filtrar Pagos por Estado
```http
GET /api/pagos/estado/{estado}

Estados válidos: PENDIENTE, CONFIRMADO, RECHAZADO, REEMBOLSADO

Response (200 OK):
[
  { ... pagos en ese estado ... }
]
```

### Confirmar Pago
```http
POST /api/pagos/{id}/confirmar?referenciaMercadoPago=mp_123456

Response (200 OK):
{
  "id": 1,
  "estado": "CONFIRMADO",
  "referenciaMercadoPago": "mp_123456",
  "fechaPago": "2026-02-08T15:52:04"
}
```

### Rechazar Pago
```http
POST /api/pagos/{id}/rechazar?motivo=Tarjeta rechazada

Response (200 OK):
{
  "id": 1,
  "estado": "RECHAZADO",
  "observaciones": "Pago rechazado. Motivo: Tarjeta rechazada"
}
```

### Reembolsar Pago
```http
POST /api/pagos/reembolsar
Content-Type: application/json

{
  "pagoId": 1,
  "motivo": "Vuelo cancelado",
  "observaciones": "Reembolso completo",
  "montoReembolso": null
}

Response (200 OK):
{
  "id": 1,
  "estado": "REEMBOLSADO",
  "observaciones": "Pago reembolsado. Motivo: Vuelo cancelado"
}
```

### Reembolso Parcial
```http
POST /api/pagos/{id}/reembolso-parcial?motivo=Cambio de plan&monto=50000

Response (200 OK):
{
  "id": 1,
  "estado": "REEMBOLSADO",
  "monto": 100000.0
}
```

### Obtener Total de Pagos Confirmados
```http
GET /api/pagos/vuelo/{vueloId}/total-confirmado

Response (200 OK):
100000.0
```

### Verificar Pago Confirmado
```http
GET /api/pagos/vuelo/{vueloId}/tiene-pago/{montoRequerido}

Response (200 OK):
true
```

### Procesar Webhook
```http
POST /api/pagos/webhook
Content-Type: application/json
x-request-id: 123456789
x-signature: abcdefgh

{
  "id": "webhook_123",
  "type": "payment.created",
  "data": {
    "id": "payment_123456789"
  }
}

Response (204 No Content)
```

## 📊 Estados de Pago

| Estado | Descripción | Transiciones |
|--------|-------------|--------------|
| **PENDIENTE** | Pago iniciado pero no confirmado | → CONFIRMADO, RECHAZADO |
| **CONFIRMADO** | Pago validado por Mercado Pago | → REEMBOLSADO |
| **RECHAZADO** | Pago rechazado por banco o MP | (final) |
| **REEMBOLSADO** | Dinero devuelto al cliente | (final) |

## 🔄 Flujos de Negocio

### Flujo 1: Pago Simple
```
1. Cliente solicita crear pago → POST /api/pagos
2. Sistema retorna URL de MP
3. Cliente va a MP y paga
4. MP redirige a success-url (frontend)
5. Frontend notifica al backend → POST /api/pagos/{id}/confirmar
6. Sistema marca como CONFIRMADO
```

### Flujo 2: Pago Rechazado
```
1. Cliente intenta pagar en MP
2. Pago rechazado (sin fondos, tarjeta expirada, etc.)
3. MP redirige a failure-url (frontend)
4. Frontend notifica al backend → POST /api/pagos/{id}/rechazar
5. Sistema marca como RECHAZADO
6. Cliente puede intentar otro pago
```

### Flujo 3: Reembolso
```
1. Cliente tiene pago confirmado
2. Cliente solicita cancelación
3. Admin ejecuta → POST /api/pagos/reembolsar
4. Sistema procesa reembolso en MP
5. Sistema marca como REEMBOLSADO
6. Dinero se devuelve a cliente (2-5 días hábiles)
```

### Flujo 4: Múltiples Pagos Parciales
```
1. Vuelo cuesta 1,000,000 COP
2. Cliente paga 500,000 en cuota 1
3. Cliente paga 500,000 en cuota 2
4. GET /api/pagos/vuelo/{id}/total-confirmado retorna 1,000,000
5. Vuelo puede procesarse
```

## 🧪 Pruebas

### Ejecutar Pruebas Unitarias
```bash
cd backend/
./gradlew test -i
```

### Ejecutar Pruebas de Integración
```bash
cd backend/
./gradlew test --tests "*IntegrationTest" -i
```

### Ejecutar Prueba Específica
```bash
./gradlew test --tests "*MercadoPagoServiceTest*" -i
```

### Cobertura de Pruebas
```bash
./gradlew test jacocoTestReport
# Reporte en: backend/build/reports/jacoco/test/html/index.html
```

## 🛡️ Seguridad

### Validación de Webhook
```java
// En MercadoPagoService
boolean isValid = mercadoPagoService.validarWebhook(xRequestId, xSignature);
```

Los headers `x-request-id` y `x-signature` deben estar presentes en todos los webhooks.

### Verificación de Pago
```java
// Consultar estado real en MP
Payment payment = mercadoPagoService.consultarEstadoPago(paymentId);
```

Siempre valida el estado del pago directamente con MP, no confíes solo en webhooks.

### Rate Limiting
Para evitar abuso, implementa rate limiting en el controlador:
```properties
# Aplicar límite de 10 pagos por minuto por usuario
spring.mvc.limiters.create-payment=10
```

## 📱 Integración Frontend

### Flujo Frontend Angular
```typescript
// 1. Crear pago
pagoService.iniciarPago(pagoData).subscribe(response => {
  // 2. Redirigir a URL de pago
  window.location.href = response.urlPago;
});

// 3. De vuelta del pago (success-url)
// En componente de éxito:
pagoService.confirmarPago(pagoId, referenciaMercadoPago).subscribe(response => {
  // Mostrar confirmación
});
```

## 🐛 Troubleshooting

### Error: "Pago no encontrado en MercadoPago"
**Causa**: El ID de pago proporcionado no existe en MP.
**Solución**: Verifica que estés usando el token correcto (TEST vs LIVE).

### Error: "Token de acceso inválido"
**Causa**: `MERCADOPAGO_TOKEN` no está configurado o es inválido.
**Solución**: 
1. Verifica en https://www.mercadopago.com/account/settings/applications
2. Copia el token correcto (TEST para desarrollo)

### Webhook no se recibe
**Causa**: URL no es accesible desde MP.
**Solución**:
1. En desarrollo local: usa `ngrok http 8080`
2. En producción: configura DNS y SSL correctamente
3. Verifica en MP: Account → Settings → Webhooks

### Pago pendiente no se confirma
**Causa**: El webhook de confirmación no fue procesado.
**Solución**:
1. Verifica que el webhook esté habilitado en MP
2. Revisa logs de la aplicación
3. Confirma manualmente: `POST /api/pagos/{id}/confirmar`

## 📚 Recursos Externos

- [Documentación Mercado Pago SDK Java](https://github.com/mercadopago/sdk-java)
- [API de Mercado Pago](https://www.mercadopago.com/developers/es/reference)
- [Configuración de Webhooks](https://www.mercadopago.com/developers/es/guides/webhooks/overview)
- [Códigos de Error](https://www.mercadopago.com/developers/es/reference/payments/_payments_id/get)

## 📝 Notas de Implementación

### Limitaciones Actuales
1. El reembolso parcial está modelado pero no procesa montos diferentes del total
2. No hay límite de intentos de pago fallidos
3. Los webhooks se procesan de forma asíncrona (mejorar con colas)

### Mejoras Futuras
1. Implementar ProcessingQueue para webhooks
2. Agregar reintentos automáticos para pagos fallidos
3. Soporte para múltiples monedas (USD, ARS, etc.)
4. Pagos recurrentes/suscripciones
5. Dashboard de reportes de pagos

## 👥 Soporte

Para reportar problemas o sugerencias:
1. Revisa este documento y la sección Troubleshooting
2. Consulta los logs de la aplicación
3. Abre un issue en el repositorio del proyecto
4. Contacta al equipo de desarrollo

---

**Última actualización**: Febrero 2026
**Versión**: 1.0
**Responsable**: Equipo de Desarrollo Backend
