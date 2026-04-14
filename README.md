# LibrarySystem 📚

Sistema de gestión de bibliotecas desarrollado con **Jakarta EE 10** y desplegado sobre **Eclipse GlassFish 7**. Permite administrar usuarios, autores, libros y préstamos a través de una interfaz web moderna, responsiva y optimizada para grandes volúmenes de datos.

## Tecnologías

| Capa | Tecnología |
|------|------------|
| **Lenguaje** | Java 11 |
| **Plataforma** | Jakarta EE 10 (Servlets, JSP) |
| **Servidor** | Eclipse GlassFish 7.0.25 |
| **Base de Datos** | MySQL 8.0 |
| **Conector** | MySQL Connector/J 9.6.0 |
| **Build** | Apache Maven |
| **Frontend** | HTML5, CSS3 (variables nativas), JavaScript Vanilla |
| **Iconos** | Lucide Icons (CDN) |
| **Tipografía** | DM Serif Display / Manrope (Google Fonts) |

## Arquitectura

```
src/main/java/
├── controller/         # Lógica de negocio (BookController, UserController, etc.)
├── dao/                # Interfaces de acceso a datos (IBookDAO, IUserDAO, etc.)
│   └── impl/           # Implementaciones JDBC (BookDAO, UserDAO, etc.)
├── database/           # Conexión a base de datos (ConnectionDB)
├── model/              # Entidades (bookModel, userModel, authorModel, loanModel)
└── servlet/            # Servlets HTTP (BookServlet, LoanServlet, DashboardServlet, etc.)

src/main/webapp/
├── assets/
│   ├── css/styles.css  # Sistema de diseño completo (variables, componentes, responsive)
│   └── js/app.js       # Lógica de frontend (modales, tabs, autocomplete, AJAX forms)
├── pages/
│   ├── shared/         # Componentes reutilizables (head.jsp, sidebar.jsp)
│   ├── dashboard.jsp   # Panel de resumen estadístico
│   ├── users.jsp       # CRUD de usuarios con paginación y búsqueda
│   ├── authors.jsp     # CRUD de autores con paginación y búsqueda
│   ├── books.jsp       # CRUD de libros con autocomplete de autores
│   └── loans.jsp       # Gestión de préstamos (activos / historial)
└── index.jsp           # Redirección al dashboard
```

## Funcionalidades

### Dashboard
- Tarjetas con estadísticas en tiempo real: total de usuarios, autores, libros, préstamos activos e historial completo.

### Usuarios (`/users`)
- Listado paginado (20 registros/página) con ventana deslizante de 5 páginas.
- Barra de búsqueda del lado del servidor (`LIKE` + `LIMIT`/`OFFSET`).
- Registro y actualización con validación de email (regex) y teléfono.
- Eliminación lógica (campo `activo` BIT).

### Autores (`/authors`)
- Listado paginado con búsqueda del servidor.
- Registro y actualización con formularios modales.

### Libros (`/books`)
- Listado paginado con búsqueda por título o ISBN.
- **Autocompletado predictivo** para selección de autores (máximo 10 sugerencias vía AJAX).
- **Validación de ISBN único** en el backend (`SELECT 1 ... LIMIT 1`).
- Formularios asíncronos (AJAX) que devuelven mensajes de error sin recargar la página.

### Préstamos (`/loans`)
- **Pestaña Activos**: Filtros combinables por ID de usuario (autocomplete) y fecha.
- **Pestaña Historial**: Historial completo paginado con estado visual (badges).
- Registro de préstamos con autocomplete de usuarios y libros.
- Fecha de préstamo automática (fecha del sistema).
- Acción de devolución con fecha automática.

## Optimizaciones Implementadas

| Problemática | Solución |
|-------------|----------|
| Listas desplegables con miles de registros | Autocompletado AJAX con `LIMIT 10` |
| Paginación mostrando cientos de botones | Ventana deslizante de 5 páginas con puntos suspensivos |
| Formularios con recarga completa | Submit asíncrono con `fetch()` y respuestas JSON |
| Validación de ISBN duplicado | `SELECT 1 FROM books WHERE isbn = ? LIMIT 1` |
| Eliminación destructiva de usuarios | Borrado lógico con campo `activo` BIT |
| Filtros en préstamos activos | SQL dinámico con `StringBuilder` y parámetros opcionales |

## Requisitos Previos

- **JDK** 11 o superior
- **Apache Maven** 3.8+
- **MySQL** 8.0+
- **Eclipse GlassFish** 7.x

## Instalación y Despliegue

### 1. Base de datos

```sql
CREATE DATABASE DBlibrary;
```

> Configura las credenciales de conexión en `database/ConnectionDB.java`.

### 2. Datos de prueba (opcional)

Ejecuta el script Python para generar datos masivos:

```bash
python generate_seed.py
mysql -u root -p DBlibrary < seed_data.sql
```

Esto insertará: 978 usuarios, 1256 autores, 4751 libros y 2791 préstamos.

### 3. Compilación

```bash
mvn clean package
```

### 4. Despliegue

Copia el archivo `target/LibrarySystem-1.0-SNAPSHOT.war` al directorio `autodeploy` de GlassFish, o despliégalo desde la consola de administración.

### 5. Acceso

```
http://localhost:8080/LibrarySystem/
```

## Estructura de la Base de Datos

```
users          (id_user, name, email, phone, activo)
authors        (id_author, name, nationality)
books          (id_book, title, isbn, year, id_author FK)
loans          (id_loan, loan_date, return_date, id_user FK, id_book FK, returned)
```

## Endpoints API (JSON)

Los siguientes endpoints devuelven JSON para los componentes de autocompletado:

| Método | URL | Descripción |
|--------|-----|-------------|
| `GET` | `/users?action=apiSearch&query=texto` | Buscar usuarios (máx. 10) |
| `GET` | `/authors?action=apiSearch&query=texto` | Buscar autores (máx. 10) |
| `GET` | `/books?action=apiSearch&query=texto` | Buscar libros (máx. 10) |
| `POST` | `/books` con `action=registerAjax` | Registrar libro (JSON response) |
| `POST` | `/books` con `action=updateAjax` | Actualizar libro (JSON response) |
| `POST` | `/loans` con `action=registerAjax` | Registrar préstamo (JSON response) |

## Licencia

Este proyecto es de uso académico.
