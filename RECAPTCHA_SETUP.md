# Configuración de reCAPTCHA v3

## Estado Actual
✅ **Frontend**: Configurado con reCAPTCHA v3
- Site Key: `6LcGtGssAAAAAO8HGZPj0HOk5D1_3OxU2M6FFqpb`
- Script cargado en `index.html`: https://www.google.com/recaptcha/api.js
- Componentes Login y Register usan `RecaptchaV3Service.execute()`

✅ **Backend**: Preparado para validar reCAPTCHA v3
- `RecaptchaServiceImpl` valida tokens
- Integrado en `AuthServiceImpl` para login y register

## Características de reCAPTCHA v3
- **Invisible**: No molesta al usuario (sin CAPTCHA visible)
- **Score-based**: Retorna un score 0.0-1.0
  - 0.9+: Muy probablemente usuario legítimo
  - 0.5-0.9: Probablemente usuario legítimo
  - 0.1-0.5: Sospechoso
  - 0.0-0.1: Muy probablemente bot

## Configuración Necesaria en Backend

### Variables de Entorno
```bash
# Habilitar reCAPTCHA v3
export RECAPTCHA_ENABLED=true

# Secret Key de reCAPTCHA v3 (obtener de Google Cloud Console)
# https://console.cloud.google.com/security/recaptcha
export RECAPTCHA_SECRET_KEY="tu-secret-key-aqui"

# Score mínimo (0.0 a 1.0) - 0.5 es recomendado
export RECAPTCHA_SCORE_THRESHOLD=0.5
```

### Flujo Técnico
La URL `https://www.google.com/recaptcha/api/siteverify` se usa para:
1. Frontend genera token con `RecaptchaV3Service.execute('login'|'register')`
2. Frontend envía token en payload (recaptchaToken)
3. Backend llama a `recaptchaService.validarToken(token)`
4. Backend verifica con Google:
   ```
   POST https://www.google.com/recaptcha/api/siteverify
   secret=RECAPTCHA_SECRET_KEY&response=TOKEN
   ```
5. Google retorna: `{ success: boolean, score: 0.0-1.0, action: string, challenge_ts: timestamp, hostname: string }`
6. Backend compara `score >= RECAPTCHA_SCORE_THRESHOLD`

## Cómo Obtener las Credenciales

### Site Key y Secret Key
1. Ve a [Google Cloud Console - reCAPTCHA](https://console.cloud.google.com/security/recaptcha)
2. Selecciona tu proyecto o crea uno nuevo
3. Haz clic en tu key: `6LcGtGssAAAAAO8HGZPj0HOk5D1_3OxU2M6FFqpb`
4. Tipo: **reCAPTCHA v3** (no Enterprise)
5. Copia el **Secret Key** (diferente al Site Key)

## Testing

### Con reCAPTCHA deshabilitado (desarrollo):
```bash
export RECAPTCHA_ENABLED=false
```
El backend aceptará cualquier token (o ninguno).

### Con reCAPTCHA habilitado:
```bash
export RECAPTCHA_ENABLED=true
export RECAPTCHA_SECRET_KEY="tu-secret-key"
```
El frontend automáticamente generará tokens en login/register y el backend los validará.

## Arquitectura de la Integración

```
Frontend (Angular 21)
  ├── index.html: <script src="...api.js?render=SITE_KEY">
  ├── app.config.ts: RECAPTCHA_V3_SITE_KEY configurada
  ├── LoginComponent:
  │   └── recaptchaV3Service.execute('login') → token Observable
  └── RegisterComponent:
      └── recaptchaV3Service.execute('register') → token Observable
                      ↓
            HTTP POST con recaptchaToken
                      ↓
Backend (Spring Boot 4.0.2)
  ├── AuthController: /api/auth/login, /api/auth/register
  ├── AuthServiceImpl:
  │   └── recaptchaService.validarToken(token)
  └── RecaptchaServiceImpl:
      └── POST a https://www.google.com/recaptcha/api/siteverify
          con secret + token → valida score >= threshold
```

## Próximos Pasos

1. **Obtener Secret Key** de Google Cloud Console
2. **Configurar variables de entorno** en tu backend
3. **Reiniciar backend** con las nuevas variables
4. **Probar** login y register en el frontend

## Troubleshooting

### Error: "Validación reCAPTCHA fallida"
- Verifica que `RECAPTCHA_ENABLED=true`
- Verifica que `RECAPTCHA_SECRET_KEY` sea correcto
- Revisa logs del backend para ver el mensaje de error detallado
- Asegúrate de que el token no haya expirado

### reCAPTCHA no carga en el frontend
- Verifica que el script en `index.html` cargue correctamente
- Abre DevTools → Console para ver errores de JavaScript
- Verifica que el Site Key sea correcto: `6LcGtGssAAAAAO8HGZPj0HOk5D1_3OxU2M6FFqpb`

### Score muy bajo (muchos rechazos)
- Ajusta `RECAPTCHA_SCORE_THRESHOLD` a un valor más bajo (ej: 0.3)
- Usuarios legítimos típicamente tienen scores > 0.5
- Bots tienen scores < 0.3

### Score muy alto (muchos aceptos, sin validación)
- Ajusta `RECAPTCHA_SCORE_THRESHOLD` a un valor más alto (ej: 0.8)
- Monitorea en Google reCAPTCHA Analytics

## Diferencias con reCAPTCHA Enterprise

reCAPTCHA v3 (lo que usamos):
- ✅ Gratis
- ✅ Score invisible (0.0-1.0)
- ✅ Simple de implementar
- ❌ Análisis básico

reCAPTCHA Enterprise:
- ❌ Pagado
- ✅ Análisis avanzado de riesgo
- ✅ Assessment labels
- ✅ Risk analysis (fraud signals)
- ✅ Soporte técnico de Google

Para este proyecto, **reCAPTCHA v3 es suficiente**.

---

**Estado**: ✅ Configurado y listo
**Versión**: reCAPTCHA v3
**Última actualización**: 2026-02-15
