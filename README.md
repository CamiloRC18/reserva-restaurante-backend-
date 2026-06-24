# 🍽️ Sistema de Reservas para Restaurante — Backend

API REST construida con **Java 17** y **Spring Boot** para gestionar mesas y reservas de un restaurante. Permite consultar mesas disponibles, crear reservas validando disponibilidad de horario, y gestionar el estado de cada reserva.

Este proyecto forma parte de un sistema fullstack. El frontend en React se encuentra en un repositorio separado.

---

## 🛠️ Tecnologías utilizadas

| Tecnología | Versión | Para qué se usa |
|---|---|---|
| Java | 17 | Lenguaje principal |
| Spring Boot | 3.x | Framework base del proyecto |
| Spring Web | - | Crear los endpoints REST |
| Spring Data JPA | - | Comunicación con la base de datos sin escribir SQL manual |
| Hibernate | - | ORM que traduce las entidades Java a tablas SQL |
| Spring Validation | - | Validar los datos que llegan del frontend |
| H2 Database | - | Base de datos en memoria para desarrollo local |
| Lombok | - | Reduce código repetitivo (getters, setters, constructores) |
| Maven | - | Gestor de dependencias y construcción del proyecto |

---

## 📁 Estructura del proyecto

```
src/main/java/com/miapp/reservarestaurante/
│
├── ReservaRestauranteApplication.java   ← Punto de entrada de la aplicación
│
├── model/                               ← Entidades que representan las tablas de la BD
│   ├── Mesa.java                        ← Representa una mesa física del restaurante
│   ├── Reserva.java                     ← Representa una reserva hecha por un cliente
│   └── EstadoReserva.java               ← Enum con los posibles estados de una reserva
│
├── repository/                          ← Interfaces que se comunican con la base de datos
│   ├── MesaRepository.java              ← Consultas sobre la tabla "mesas"
│   └── ReservaRepository.java           ← Consultas sobre la tabla "reserva"
│
├── service/                             ← Lógica de negocio del sistema
│   ├── MesaService.java                 ← Operaciones sobre mesas
│   └── ReservaService.java              ← Lógica de creación y validación de reservas
│
├── controller/                          ← Endpoints HTTP que expone la API
│   ├── MesaController.java              ← Rutas /api/mesas
│   └── ReservaController.java           ← Rutas /api/reservas
│
├── dto/                                 ← Objetos que definen qué datos entran y salen
│   ├── ReservaRequestDTO.java           ← Lo que el frontend envía al crear una reserva
│   └── ReservaResponseDTO.java          ← Lo que el backend devuelve al frontend
│
└── exception/                           ← Manejo de errores personalizado
    ├── RecursoNoEncontradoException.java ← Error cuando algo no existe en la BD
    ├── MesaNoDisponibleException.java    ← Error cuando la mesa está ocupada o sin capacidad
    └── GlobalExceptionHandler.java       ← Intercepta todos los errores y devuelve JSON limpio

src/main/resources/
└── application.properties               ← Configuración de la app (BD, puerto, etc.)
```

---

## 🧠 ¿Qué hace cada capa?

### `model/` — Las entidades

Son clases Java que Hibernate convierte automáticamente en tablas de base de datos.

**`Mesa.java`**
Representa una mesa física del restaurante. Tiene `id`, `nombre` (ej: "Mesa VIP"), `capacidad` (número de personas que caben) y `activa` (si la mesa está disponible para reservar o no).

**`Reserva.java`**
Representa una reserva hecha por un cliente. Está relacionada con una `Mesa` a través de `@ManyToOne` — lo que significa que muchas reservas pueden apuntar a la misma mesa (en diferentes fechas y horarios). Tiene los datos del cliente, la fecha, la hora, cuántas personas van, y el estado actual de la reserva.

**`EstadoReserva.java`**
Es un `enum` — una lista cerrada de valores posibles para el estado de una reserva: `PENDIENTE`, `CONFIRMADA`, `CANCELADA`, `COMPLETADA`. Se usa enum en vez de String para evitar errores de tipeo y restringir los valores posibles.

---

### `repository/` — La comunicación con la base de datos

Son interfaces que extienden `JpaRepository`. Spring Data JPA genera automáticamente la implementación — no se escribe SQL manual.

**`MesaRepository.java`**
Además de los métodos heredados (`findAll`, `findById`, `save`, etc.), tiene:
- `findByCapacidadGreaterThanEqual(Integer capacidad)` → busca mesas cuya capacidad sea mayor o igual al número de personas solicitado.

**`ReservaRepository.java`**
Tiene métodos personalizados para las consultas que necesita el sistema:
- `findByFecha(LocalDate fecha)` → trae todas las reservas de un día específico.
- `findByMesaIdAndFecha(Long mesaId, LocalDate fecha)` → trae las reservas de una mesa en un día específico. Se usa para validar disponibilidad de horario.

---

### `service/` — La lógica de negocio

Es la capa más importante. Aquí viven las reglas del sistema. El controller nunca toma decisiones — solo llama al service.

**`MesaService.java`**
Expone operaciones sobre mesas: listar todas, buscar por id. Lanza `RecursoNoEncontradoException` si se busca una mesa que no existe.

**`ReservaService.java`**
Contiene la lógica principal del sistema. Cuando se crea una reserva, valida tres cosas en orden:

1. **¿Existe la mesa?** → Si no existe, lanza `RecursoNoEncontradoException`.
2. **¿La mesa tiene capacidad suficiente?** → Si el número de personas supera la capacidad, lanza `MesaNoDisponibleException`.
3. **¿La mesa ya está ocupada en ese horario?** → Consulta todas las reservas activas de esa mesa ese día y verifica si alguna se superpone con el horario solicitado, usando una ventana fija de 2 horas por reserva. Si hay cruce, lanza `MesaNoDisponibleException`.

La lógica de detección de cruce de horarios funciona así: dos rangos de tiempo se superponen si el inicio del nuevo es anterior al fin del existente, Y el inicio del existente es anterior al fin del nuevo.

```java
boolean seSuperponen = horaSolicitada.isBefore(finExistente)
                    && inicioExistente.isBefore(finSolicitado);
```

---

### `controller/` — Los endpoints de la API

Reciben las peticiones HTTP, las pasan al service, y devuelven la respuesta. No contienen lógica de negocio.

**`MesaController.java`** — base: `/api/mesas`

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/mesas` | Lista todas las mesas |
| GET | `/api/mesas/{id}` | Obtiene una mesa por su id |

**`ReservaController.java`** — base: `/api/reservas`

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/reservas` | Crea una reserva nueva (valida disponibilidad) |
| GET | `/api/reservas?fecha=2026-06-20` | Lista todas las reservas de una fecha |
| GET | `/api/reservas/{id}` | Obtiene una reserva por su id |
| PATCH | `/api/reservas/{id}/estado?estado=CONFIRMADA` | Cambia el estado de una reserva |

---

### `dto/` — Los objetos de transferencia de datos

Definen exactamente qué información entra y sale de la API. Se usan en vez de las entidades directamente por dos razones: seguridad (no se exponen campos internos) y control (se define exactamente qué se recibe y qué se devuelve).

**`ReservaRequestDTO.java`** — lo que el frontend envía al crear una reserva:
```json
{
  "mesaId": 1,
  "cliente": "Juan Pérez",
  "fecha": "2026-06-20",
  "hora": "19:30:00",
  "numeroPersonas": 2
}
```
Incluye validaciones automáticas con anotaciones como `@NotBlank`, `@NotNull`, `@FutureOrPresent`, `@Min`. Si alguna falla, Spring responde automáticamente con `400 Bad Request` y el detalle del error.

**`ReservaResponseDTO.java`** — lo que el backend devuelve:
```json
{
  "id": 1,
  "mesaNombre": "Mesa 1",
  "cliente": "Juan Pérez",
  "fecha": "2026-06-20",
  "hora": "19:30:00",
  "numeroPersonas": 2,
  "estado": "PENDIENTE"
}
```
Nótese que devuelve `mesaNombre` (texto legible) en vez del objeto `Mesa` completo.

Ambos DTOs usan `record` de Java — una forma compacta de declarar clases inmutables que solo transportan datos, sin necesidad de Lombok.

---

### `exception/` — El manejo de errores

**`RecursoNoEncontradoException.java`**
Se lanza cuando se busca algo que no existe en la base de datos (una mesa o reserva por id). El `GlobalExceptionHandler` la convierte en respuesta `404 Not Found`.

**`MesaNoDisponibleException.java`**
Se lanza cuando la mesa no tiene capacidad suficiente o ya está reservada en ese horario. Se convierte en respuesta `409 Conflict`.

**`GlobalExceptionHandler.java`**
Clase anotada con `@RestControllerAdvice` que intercepta automáticamente todas las excepciones lanzadas en cualquier controller. En vez de que Spring devuelva un error genérico `500`, devuelve un JSON claro con el código HTTP correcto y un mensaje descriptivo:

```json
{
  "timestamp": "2026-06-23T20:00:00",
  "status": 404,
  "mensaje": "Reserva no encontrada con id 99"
}
```

También maneja los errores de validación del DTO, devolviendo exactamente qué campo falló y por qué.

---

## ▶️ Cómo correr el proyecto

### Requisitos
- Java 17
- Maven

### Pasos

```bash
# 1. Clonar el repositorio
git clone https://github.com/tuUsuario/reserva-restaurante-backend.git

# 2. Entrar a la carpeta
cd reserva-restaurante-backend

# 3. Correr la aplicación
mvn spring-boot:run
```

La app queda disponible en `http://localhost:8080`.

Al iniciar, Hibernate crea automáticamente las tablas `mesas` y `reserva` en la base de datos H2.

---

## 🗄️ Consola H2 (base de datos en memoria)

Con la app corriendo, entra a:
```
http://localhost:8080/h2-console
```

Conéctate con:
```
JDBC URL:  jdbc:h2:mem:reservas_db
User Name: sa
Password:  (vacío)
```

Desde ahí puedes ver las tablas e insertar datos de prueba manualmente con SQL.

---

## 📌 Próximos pasos

- Agregar autenticación con Spring Security + JWT
- Enviar email de confirmación al crear una reserva (JavaMailSender)
- Agregar tests unitarios con JUnit 5 y Mockito
- Migrar a PostgreSQL con Docker
- Construir el frontend en React que consuma esta API
- Desplegar en Railway o Render