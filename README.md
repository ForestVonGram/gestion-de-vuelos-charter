<div align="center">

# ✈️ AstraNimbus - astranimbus.com

**Plataforma digital para la gestión integral de vuelos chárter privados**

![Version](https://img.shields.io/badge/versión-1.0-blue?style=for-the-badge)
![Backend](https://img.shields.io/badge/Backend-Java%20%7C%20Spring%20Boot-orange?style=for-the-badge&logo=springboot)
![Frontend](https://img.shields.io/badge/Frontend-Angular%20%7C%20TypeScript-red?style=for-the-badge&logo=angular)
![BD](https://img.shields.io/badge/BD-PostgreSQL%20%7C%20MongoDB-336791?style=for-the-badge&logo=postgresql)

*Desarrollado por **PAELDAV Corp.***

</div>

---

## 📋 Tabla de Contenidos

- [Descripción](#-descripción)
- [Características principales](#-características-principales)
- [Stack tecnológico](#-stack-tecnológico)
- [Arquitectura del sistema](#-arquitectura-del-sistema)
- [Estructura del proyecto](#-estructura-del-proyecto)
- [Requisitos previos](#-requisitos-previos)
- [Instalación y configuración](#-instalación-y-configuración)
- [Uso del sistema](#-uso-del-sistema)
- [Roles de usuario](#-roles-de-usuario)
- [Integraciones externas](#-integraciones-externas)
- [Decisiones de arquitectura (ADRs)](#-decisiones-de-arquitectura-adrs)
- [Autores](#-autores)

---

## 📖 Descripción

**AstraNimbus** es una aplicación web diseñada para centralizar y automatizar la planificación, operación y supervisión de vuelos chárter privados dentro de una empresa aeronáutica.

El sistema cubre el ciclo completo de un vuelo chárter: desde la solicitud y aprobación, hasta la asignación de aeronave y tripulación, programación, seguimiento operativo y cierre del servicio. Busca garantizar **trazabilidad operativa**, **control de recursos** y una gestión eficiente que elimine procesos manuales dispersos.

### 🎯 Problema que resuelve

AstraNimbus responde a la necesidad de coordinar vuelos chárter de forma organizada y centralizada, permitiendo:

- Controlar la disponibilidad de aeronaves en tiempo real.
- Asignar tripulación de manera estructurada y trazable.
- Hacer seguimiento del estado de cada vuelo.
- Registrar mantenimientos y controlar la operatividad técnica.
- Gestionar clientes y solicitudes de servicio desde un único sistema.

---

## ✨ Características principales

### Para Tripulación / Pilotos
- 📅 Consulta de historial de vuelos (con filtros por estado y fecha)
- 👥 Visualización de la tripulación asignada
- 📝 Generación de reportes de incidencias y llamados de atención
- 📄 Gestión de certificados (carga, actualización y control de vencimientos)

### Para Administradores
- 👤 Gestión completa del personal operativo (tripulación)
- 📊 Visualización de estadísticas, vuelos, reportes y certificaciones
- 🛩️ Administración de aeronaves y disponibilidad de flota
- 📈 Acceso a métricas globales del negocio

### Para Clientes
- ✈️ Agendamiento de vuelos chárter
- 🔍 Consulta de historial de vuelos contratados
- 💳 Procesamiento de pagos en línea

### General
- 🔐 Autenticación segura con roles diferenciados
- 🌗 Modo oscuro / claro
- 📱 Accesible desde cualquier dispositivo con conexión a internet
- 📧 Notificaciones por correo electrónico

---

## 🛠️ Stack tecnológico

| Capa | Tecnología |
|------|------------|
| **Frontend** | Angular · TypeScript · HTML5 · Tailwind CSS |
| **Backend** | Java · Spring Boot · Spring Security |
| **BD Principal** | PostgreSQL (relacional) |
| **BD Espejo** | MongoDB (documental / respaldo) |
| **Almacenamiento** | Cloudinary (imágenes) |
| **Pagos** | Mercado Pago |
| **Notificaciones** | Servicio externo de correo (email/push) |
| **Seguridad** | JWT · Google reCAPTCHA |
| **ORM** | JPA / Hibernate |
| **CI/CD** | Pipeline: Commit → Build → Test → Artifact → Deploy → Monitoring |

---

## 🏗️ Arquitectura del sistema

AstraNimbus sigue el **Modelo C4** para describir su arquitectura en cuatro niveles:

### Nivel 1 – Contexto
El sistema interactúa con tres tipos de actores (Cliente, Tripulación/Pilotos, Administrador) y cuatro sistemas externos:
- **Cloudinary** – gestión de imágenes
- **Mercado Pago** – procesamiento de pagos
- **Servicio de Notificaciones** – correos y notificaciones push
- **Google reCAPTCHA** – protección contra bots

### Nivel 2 – Contenedores
| Contenedor | Tecnología | Rol |
|------------|------------|-----|
| Web App | Angular / TypeScript | Interfaz de usuario principal |
| API REST | Java / Spring Boot | Lógica de negocio y servicios |
| BD Principal | PostgreSQL | Persistencia transaccional |
| BD Espejo | MongoDB | Respaldo documental |

### Nivel 3 – Componentes del Backend
- `AuthController` – Autenticación y registro
- `VueloController` / `DisponibilidadController` – Gestión de vuelos
- `MetricasController` – Métricas globales
- `ReporteService` / `ExportarReporteService` – Historial y facturas
- `AlertaService` – Notificaciones
- `PagoService` – Integración con Mercado Pago
- `ImagenAeronaveService` – Gestión de imágenes vía Cloudinary

### Nivel 4 – Código

Ver sección [Estructura del proyecto](#-estructura-del-proyecto).

---

## 📁 Estructura del proyecto

```
/AstraNimbus/
├── /backend/
│   └── /src/
│       └── /main/java/
│           └── /com/paeldav/backend/
│               ├── /application/          # DTOs, mappers, servicios
│               ├── /domain/               # Entidades y enumeraciones
│               ├── /exception/            # Manejo de excepciones
│               ├── /infraestructure/      # Config, repositorios, seguridad
│               └── /presentation.controller/  # Controladores REST
│       └── /resources/
│           └── application.properties
│   ├── .gitignore
│   └── build.gradle
│
├── /frontend-files/
│   └── /frontend/
│       └── /src/
│           ├── /app/
│           │   ├── /features/
│           │   ├── /pages/
│           │   ├── /services/
│           │   ├── /shared/
│           │   └── app.config.ts
│           ├── /assets/
│           │   ├── /fuentes/
│           │   ├── /iconos/
│           │   ├── /images/
│           │   └── /logo/
│           ├── index.html
│           └── main.ts
│
└── /docs/
    ├── C4Model/
    └── arquitectura/
```

---

## ✅ Requisitos previos

Para ejecutar AstraNimbus se necesita:

- **JDK 17+** (para el backend)
- **Node.js 18+** y **npm** (para el frontend Angular)
- **PostgreSQL 14+**
- **MongoDB 6+**
- Credenciales de:
  - Cloudinary
  - Mercado Pago
  - Servicio de correo (SMTP o proveedor externo)
  - Google reCAPTCHA

> Para **usar** la aplicación como usuario final, solo se necesita un navegador web moderno (Chrome, Edge, Firefox, etc.) y conexión a internet estable.

---

## 🚀 Instalación y configuración

### Backend

```bash
# 1. Clonar el repositorio
git clone https://github.com/PAELDAV/AstraNimbus.git
cd AstraNimbus/backend

# 2. Configurar variables de entorno en application.properties
#    (BD, Cloudinary, Mercado Pago, correo, reCAPTCHA, JWT secret)

# 3. Compilar y ejecutar
./gradlew bootRun
```

### Frontend

```bash
cd AstraNimbus/frontend-files/frontend

# 1. Instalar dependencias
npm install

# 2. Ejecutar en modo desarrollo
ng serve

# 3. Abrir en el navegador
# http://localhost:4200
```

---

## 🖥️ Uso del sistema

### 1. Registro
Completa todos los campos requeridos, acepta las políticas de privacidad y los términos y condiciones, y crea tu cuenta.

### 2. Inicio de sesión
Ingresa con tu correo registrado y contraseña. En caso de olvidarla, usa la opción **¿Olvidaste tu contraseña?** para recibir un código de verificación por correo.

### 3. Dashboard principal
Desde el dashboard accedes a todas las funcionalidades según tu rol. El ícono de la esquina superior derecha da acceso rápido a **Mi Perfil** y **Mis Vuelos**.

### 4. Navegación
Puedes navegar con scroll o usando la barra lateral. También puedes alternar entre modo oscuro y claro.

---

## 👥 Roles de usuario

| Rol | Descripción |
|-----|-------------|
| **Cliente** | Solicita y agenda vuelos chárter; consulta su historial |
| **Tripulación / Piloto** | Gestiona sus vuelos, reportes y certificados |
| **Operario de logística** | Mantiene la información técnica de las aeronaves |
| **Ayudante de mantenimiento** | Registra y controla mantenimientos de aeronaves |
| **Administrador** | Supervisión global del sistema, métricas y gestión del personal |

---

## 🔌 Integraciones externas

| Servicio | Propósito |
|----------|-----------|
| **Cloudinary** | Almacenamiento y optimización de imágenes de aeronaves |
| **Mercado Pago** | Procesamiento de pagos en línea para vuelos chárter |
| **Servicio de correo** | Confirmaciones, actualizaciones de estado y alertas |
| **Google reCAPTCHA** | Protección de formularios contra bots |

---

## 📐 Decisiones de arquitectura (ADRs)

| ADR | Decisión | Justificación |
|-----|----------|---------------|
| ADR-01 | Frontend en Angular + app móvil en Flutter | SPA escalable + desarrollo multiplataforma con una sola base de código |
| ADR-02 | Backend como API REST con Spring Boot | Ecosistema maduro, seguro y ampliamente usado en aplicaciones empresariales |
| ADR-03 | Java + Spring Boot para el backend | Estabilidad, productividad y ecosistema sólido para APIs |
| ADR-04 | PostgreSQL como BD principal | Cumplimiento ACID y soporte para relaciones complejas entre entidades |
| ADR-05 | MongoDB como BD espejo y documental | Flexibilidad para históricos y auditorías sin afectar la BD transaccional |
| ADR-06 | Acceso a datos con JPA/Hibernate | Separación clara entre lógica de negocio y persistencia |
| ADR-07 | Mercado Pago como pasarela de pagos | Amplio uso en Latinoamérica, SDK oficial y soporte de webhooks |
| ADR-08 | Notificaciones externalizadas | Desacopla la lógica de negocio del envío de mensajes |
| ADR-09 | Autenticación centralizada + reCAPTCHA | Control de acceso por roles y protección contra ataques automatizados |

---

## ✍️ Autores

Desarrollado con ❤️ por el equipo **PAELDAV Corp.**

| Nombre | Rol |
|--------|-----|
| **Juan Pablo López Gómez** | Desarrollador |
| **Elkin Bermúdez Grajales** | Desarrollador |
| **David Gómez Ramírez** | Desarrollador |

---

<div align="center">

*AstraNimbus © 2026 – PAELDAV Corp.*

</div>
