# ServiClick

> **"Conectando el talento local con la necesidad del cliente."**

ServiClick es una aplicación móvil nativa para Android diseñada para actuar como un puente directo e independiente entre los clientes y los profesionales o comercios locales. Desarrollada como un Proyecto de Fin de Grado (TFG), la plataforma resuelve los problemas de opacidad, comisiones abusivas y falta de control de los directorios tradicionales mediante un ecosistema bidireccional y transaccional en tiempo real.

---

## 🚀 Características Principales (Arquitectura de Doble Rol)

La aplicación implementa dos flujos de experiencia de usuario completamente diferenciados bajo una misma base de código, dictados por el rol asignado al perfil del usuario:

### 👤 Rol Cliente
* **Búsqueda y Filtrado Avanzado:** Exploración unificada de comercios y profesionales de todos los sectores dentro de su comunidad autónoma.
* **Perfiles Detallados:** Consulta de escaparates digitales, catálogos de servicios, tarifas y duración estimada.
* **Agendamiento en Tiempo Real:** Reserva inmediata de citas seleccionando fechas y franjas horarias directamente desde la disponibilidad del profesional.
* **Chat Interno:** Comunicación directa y fluida integrada con cada solicitud de trabajo.
* **Sistema de Reseñas Verificado:** Posibilidad de valorar (puntuación de 1 a 5 con saltos de 0.5) y comentar la experiencia tras recibir el servicio.

### 💼 Rol Empresa / Profesional
* **Gestión del Escaparate Digital:** Personalización completa del perfil comercial (nombre, descripción, categoría, datos de contacto e imágenes).
* **Catálogo de Servicios Autónomo:** Configuración del listado de servicios ofrecidos, asignando precios individuales y tiempos de ejecución.
* **Control Absoluto de Agenda:** Flexibilidad total para gestionar horarios comerciales, bloquear días de vacaciones o marcar días inhábiles de forma semanal.
* **Administrador de Citas:** Panel dinámico para controlar el estado del ciclo de vida de las reservas (Pendientes, Aceptadas, Finalizadas, Pagadas o Canceladas).

---

## 🛠️ Stack Tecnológico & Arquitectura

Para garantizar la robustez, mantenibilidad y escalabilidad exigidas en una ingeniería de software, ServiClick se ha desarrollado utilizando los estándares de la industria moderna de Android:

* **Lenguaje:** [Kotlin](https://kotlinlang.org/) (100% Nativo).
* **Diseño de Interfaz:** [Jetpack Compose](https://developer.android.com/jetpack/compose), adoptando el paradigma de **Programación Declarativa** para construir interfaces fluidas y profesionales.
* **Arquitectura de Software:** **Clean Architecture** estructurada estrictamente en 3 capas independientes para asegurar un bajo acoplamiento y alta cohesión:
    * `presentation` (UI, ViewModels, States)
    * `domain` (Business Logic, Models, Use Cases)
    * `data` (Repositories, Data Sources, Mappers)
* **Patrón de Arquitectura de UI:** **MVVM** (Model-View-ViewModel) combinado con un flujo reactivo controlado por **UDF** (Flujo Unidireccional de Datos).
* **Backend as a Service (BaaS):** [Firebase](https://firebase.google.com/) para una infraestructura elástica sin mantenimiento de servidores:
    * **Firebase Authentication:** Registro y login seguro de usuarios.
    * **Cloud Firestore:** Base de datos **NoSQL** orientada a documentos para la sincronización transaccional en tiempo real.
* **Inyección de Dependencias:** [Dagger Hilt](https://developer.android.com/training/dependency-injection/hilt-android) para simplificar el ciclo de vida de los componentes y favorecer el desacoplamiento.