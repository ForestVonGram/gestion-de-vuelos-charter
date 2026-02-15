# Implementación de reCAPTCHA v3 - Resumen

## ✅ Completado

### Frontend
- **LoginComponent**: Integrado con reCAPTCHA v3 (RecaptchaV3Service)
- **RegisterComponent**: Integrado con reCAPTCHA v3 (RecaptchaV3Service)
- **index.html**: Script de reCAPTCHA v3 incluido (`api.js`)
- **app.config.ts**: Site Key configurada con RECAPTCHA_SETTINGS

### Backend
- **RecaptchaServiceImpl**: Validación de tokens lista
- **AuthServiceImpl**: Validación en login y register
- **application.properties**: Configuración lista

## 🔧 Pasos para Completar la Configuración

### 1. Obtener Secret Key de reCAPTCHA v3

1. Ve a: https://console.cloud.google.com/security/recaptcha
2. Asegúrate de estar en el proyecto correcto
3. Haz clic en la key: `6LcGtGssAAAAAO8HGZPj0HOk5D1_3OxU2M6FFqpb`
4. Verifica que sea **reCAPTCHA v3** (no Enterprise)
5. En "Key settings", copia el **Secret Key** (diferente al Site Key)

### 2. Configurar Variables de Entorno - Opción A (Desarrollo Local)

En tu terminal o archivo `.env`:
```bash
export RECAPTCHA_ENABLED=true
export RECAPTCHA_SECRET_KEY="tu-secret-key-copiado-del-paso-1"
export RECAPTCHA_SCORE_THRESHOLD=0.5
```

Luego reinicia el backend:
```bash
cd backend
./gradlew bootRun
```

### 3. Configurar Variables de Entorno - Opción B (Sistema Operativo)

**macOS/Linux:**
```bash
# Agregar al archivo ~/.zshrc o ~/.bash_profile
export RECAPTCHA_ENABLED=true
export RECAPTCHA_SECRET_KEY="tu-secret-key-aqui"
export RECAPTCHA_SCORE_THRESHOLD=0.5

# Recargar el archivo
source ~/.zshrc
```

**Windows:**
```cmd
setx RECAPTCHA_ENABLED true
setx RECAPTCHA_SECRET_KEY "tu-secret-key-aqui"
setx RECAPTCHA_SCORE_THRESHOLD 0.5
```

### 4. Verificar la Configuración

1. Inicia el backend
2. Revisa los logs para confirmar que reCAPTCHA está habilitado:
   ```
   INFO: reCAPTCHA esta habilitado
   ```

3. Abre el frontend en http://localhost:4200
4. Intenta hacer login o registro
5. Verifica en DevTools → Network que se envíe el `recaptchaToken`

## 📊 Flujo de Validación

```
Usuario intenta Login/Register
    ↓
Frontend: RecaptchaV3Service.execute('login'|'register') genera token
    ↓
Frontend: HTTP POST a /api/auth/login o /api/auth/register con recaptchaToken
    ↓
Backend: AuthServiceImpl llama a recaptchaService.validarToken()
    ↓
Backend: Envía token + secret a Google reCAPTCHA
    ↓
Google: Retorna { success: true/false, score: 0.0-1.0 }
    ↓
Backend: Compara score con threshold (0.5 por defecto)
    ↓
Si válido (score >= threshold): Procede con login/register
Si inválido (score < threshold): Retorna error "Validación reCAPTCHA fallida"
```

## 🧪 Testing

### Modo Desarrollo (reCAPTCHA Deshabilitado)
```bash
export RECAPTCHA_ENABLED=false
```
El sistema funcionará sin validar reCAPTCHA (útil para testing)

### Modo Producción (reCAPTCHA Habilitado)
```bash
export RECAPTCHA_ENABLED=true
export RECAPTCHA_SECRET_KEY="tu-secret-key"
export RECAPTCHA_SCORE_THRESHOLD=0.5
```

## 🐛 Debugging

### Ver el token en DevTools
```javascript
// En console del navegador
// El frontend automáticamente genera tokens, verifica en Network
// POST /api/auth/login → busca recaptchaToken en el payload
```

### Ver respuesta del servidor
En DevTools → Network → Request a /api/auth/login
Busca la respuesta JSON (token, error, etc.)

### Logs del Backend
```bash
# Ver logs con reCAPTCHA details
grep -i recaptcha logs/application.log
```

## ⚙️ Configuración Avanzada

### Ajustar Score Threshold
```bash
# Más permisivo (acepta más bots):
export RECAPTCHA_SCORE_THRESHOLD=0.3

# Más restrictivo (rechaza usuarios legítimos):
export RECAPTCHA_SCORE_THRESHOLD=0.8
```

Rangos típicos:
- 0.9+: Muy probablemente humano
- 0.5-0.9: Probablemente humano
- 0.1-0.5: Sospechoso
- 0.0-0.1: Muy probablemente bot

## 📝 Archivos Modificados

Frontend:
- `src/index.html` - Script de reCAPTCHA v3 (api.js)
- `src/app/app.config.ts` - RECAPTCHA_SETTINGS con Site Key
- `src/app/pages/auth/login/login.component.ts` - RecaptchaV3Service.execute()
- `src/app/pages/auth/register/register.component.ts` - RecaptchaV3Service.execute()
- `src/app/services/auth/auth.service.ts` - Método register() agregado

Backend:
- `src/main/resources/application.properties` - Configuración reCAPTCHA

Ya existentes (no requieren cambios):
- `src/main/java/.../application/service/base/RecaptchaService.java`
- `src/main/java/.../application/service/impl/RecaptchaServiceImpl.java`
- `src/main/java/.../presentation/controller/AuthController.java`
- `src/main/java/.../application/service/impl/AuthServiceImpl.java`

## ❓ FAQ

**P: ¿Funciona reCAPTCHA v3 sin Internet?**
R: No, necesita conexión a Google. Para offline, deshabilitalo con `RECAPTCHA_ENABLED=false`

**P: ¿Cuál es la diferencia entre reCAPTCHA v3 y Enterprise?**
R: 
- v3: Gratis, invisible, score-based
- Enterprise: Pagado, análisis avanzado, más funcionalidades

**P: ¿Se ve el widget de reCAPTCHA v3?**
R: No, se ejecuta silenciosamente sin mostrar nada al usuario.

**P: ¿Qué pasa si el token expira?**
R: El frontend genera un token nuevo cada vez que ejecuta `RecaptchaV3Service.execute()`, así que no hay problema.

**P: ¿Puedo cambiar el threshold del score?**
R: Sí, con `RECAPTCHA_SCORE_THRESHOLD`. Valores típicos: 0.3-0.8

**P: ¿Cómo veo los análisis de reCAPTCHA?**
R: Ve a https://console.cloud.google.com/security/recaptcha y selecciona tu key para ver analytics.

## 📞 Soporte

Si tienes problemas:
1. Revisa los logs del backend
2. Verifica las variables de entorno están configuradas
3. Abre DevTools del navegador y busca errores en Console y Network
4. Consulta: https://developers.google.com/recaptcha/docs/v3

---

**Estado**: ✅ Listo para producción
**Versión**: reCAPTCHA v3
**Última actualización**: 2026-02-15
