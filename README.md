# LibrarySystem 📚

LibrarySystem es un sistema de gestión de bibliotecas para entornos académicos. Desarrollado con **Jakarta EE 10** y diseñado para **Eclipse GlassFish 7**, permite la administración de usuarios, autores, libros y préstamos.

---

## 🛠️ Referencia Técnica

Esta sección detalla los componentes técnicos y la arquitectura principal del sistema.

### Stack Tecnológico

| Componente | Tecnología |
|------------|------------|
| **Lenguaje principal** | Java 17 |
| **Plataforma Enterprise**| Jakarta EE 10 (Servlets y plantillas JSP) |
| **Servidor de Apps** | Eclipse GlassFish 7.0.25 |
| **Base de Datos** | MySQL 8.0 |
| **Manejo de JSON** | Jackson Databind 2.18.2 |
| **Logging** | SLF4J + Log4j2 |
| **Frontend** | HTML5, CSS3 Nativo (Variables), JavaScript Vanilla |
| **Recursos Visuales** | Lucide Icons (vía CDN), Tipografías de Google Fonts |

### Arquitectura de Capas (MVC + Service Layer)

El proyecto mantiene una estricta separación de responsabilidades:

*   **`webapp/pages/` (Vistas):** Documentos JSP reusables para la interfaz gráfica.
*   **`controller/` (Controladores):** Servlets que orquestan el tráfico HTTP. Usan `Jackson` para retornar respuestas JSON estandarizadas.
*   **`service/` (Capa de Negocio):** Centraliza la lógica de validación, reglas de negocio y orquestación entre modelos.
*   **`dao/` (Acceso a Datos):** Interfaces e implementaciones JDBC. Uso intensivo de `PreparedStatement` y logging profesional con SLF4J.
*   **`model/` (Entidades):** POJOs que representan las entidades del dominio.

---

## Guía de Instalación y Despliegue

### 1. Prerrequisitos de Entorno
- **JDK 17**.
- **Apache Maven 3.8** o superior.
- **MySQL 8.0** o superior.
- **Eclipse GlassFish 7.0.25**.

### 2. Configuración de Base de Datos
```sql
CREATE DATABASE DBlibrary;

CREATE TABLE `users` (
    id_user int NOT NULL AUTO_INCREMENT,
    name varchar(100) DEFAULT NULL,
    email varchar(100) DEFAULT NULL,
    phone varchar(20) DEFAULT NULL,
    activo bit(1) DEFAULT b'1',
    PRIMARY KEY (id_user)
);


CREATE TABLE authors (
	id_author INT PRIMARY KEY AUTO_INCREMENT,
	name VARCHAR(100),
	nationality VARCHAR(50)
);

CREATE TABLE books (
	id_book INT PRIMARY KEY AUTO_INCREMENT,
	title VARCHAR(150),
	isbn VARCHAR(20),
	year INT,
	id_author INT,
	FOREIGN KEY (id_author) REFERENCES authors(id_author)
);

CREATE TABLE `loans` (
    id_loan int NOT NULL AUTO_INCREMENT,
    loan_date date DEFAULT NULL,
    return_date date DEFAULT NULL,
    id_user int DEFAULT NULL,
    id_book int DEFAULT NULL,
    returned bit(1) DEFAULT b'0',
    PRIMARY KEY (id_loan),
    KEY id_user (id_user),
    KEY id_book (id_book),
    CONSTRAINT loans_ibfk_1 FOREIGN KEY (`id_user`) REFERENCES users (id_user),
    CONSTRAINT loans_ibfk_2 FOREIGN KEY (`id_book`) REFERENCES books (id_book)
);
```
*Atención: Asegúrate de configurar correctamente tus credenciales (usuario y contraseña locales) editando el archivo de conexión correspondiente en `database/ConnectionDB.java` o el respectivo bundle properties `resources\database.properties.example`.*

### 3. Población de Datos (Opcional)
Si deseas evitar un entorno vacío y probar la eficiencia con miles de datos, ejecuta nuestra simulación:
```bash
python generate_seed.py
```
*(Carga un histórico base de 978 usuarios, 1256 autores, 4751 libros y 2791 préstamos).*

---

## Funcionalidades y Optimizaciones

### 1. Resumen Estadístico (Dashboard)
Es la página inicial del aplicativo. Expone métricas en tiempo real leyendo agregados globales para dar rápida visión operativa a administradores de recintos.

![alt text](images/dashboard.png)

### 2. Listados Inteligentes (Usuarios y Autores)
Para evitar bloqueos al cargar grandes volúmenes de datos en una sola petición web:
*   Se limitó la consulta inicial (ej. 20 resultados).
*   Se incorporó un bloque deslizante dinámico para su paginación (limita visualmente a 5 botones).
*   Las búsquedas operan del lado del servidor SQL (`LIKE %query%`) en lugar de sobrecargar memoria RAM filtrando listas inmensas desde el backend Java.
*   *Nota en Usuarios:* Tienen eliminación "lógica". El usuario es desmarcado a falso mediante el atributo `activo` sin destruir compromisos de préstamos existentes.

![alt text](images/users.png)
![alt text](images/authors.png)

### 3. Registro de Libros Anti-Duplicación
El ingreso de nuevos libros usa autocompletado en el front-end con peticiones AJAX, brindando hasta 10 respuestas cortas, evitando que el usuario envíe peticiones masivas enteras. 
Implementa prevención predictiva contra "ISBN clonados", validado usando `SELECT 1 FROM books WHERE isbn=? LIMIT 1`.

![alt text](images/bookRegister.png)

### 4. Manejo del Ciclo de Préstamos
Los componentes de préstamos en `/loans` tienen doble pestaña de visualización (Históricos vs Activos). 
Permite filtros combinados que ajustan iterativamente un `StringBuilder` en SQL, haciendo posible buscar de forma ágil desde el identificador de usuario hasta por margen de fechas.

![alt text](images/loans.png)

---

## Funcionalidades y Optimizaciones Recientes

### 1. Sistema de Notificaciones Toast
Se eliminaron los `alert()` nativos del navegador. Ahora el sistema utiliza un sistema de **Toasts** customizado en `app.js` que proporciona feedback visual no bloqueante y estéticamente superior.

### 2. Eliminación de Usuarios vía AJAX
La eliminación de usuarios ahora se realiza mediante un modal de confirmación y una petición asíncrona, evitando recargas de página innecesarias y mejorando la UX.

### 3. Estandarización de Respuestas API
Todos los controladores han sido refactores para devolver objetos JSON consistentes (`{status, message, data}`) utilizando la librería Jackson, eliminando la concatenación manual de strings.

### 4. Consultas Optimizadas y Orden Descendente
Los listados (DAO) ahora devuelven los registros en orden descendente por defecto (`ORDER BY id DESC`), asegurando que las acciones más recientes sean visibles de inmediato.

---

## Endpoints AJAX 

#### 1. Buscar Entidades (Autocomplete)

*   `GET /users?action=apiSearch&query=...`
*   `GET /authors?action=apiSearch&query=...`
*   `GET /books?action=apiSearch&query=...`

#### 2. Operaciones de Usuario (AJAX)
*   `POST /users`
    `action=register | update | delete`

#### 3. Ciclo de Préstamos
*   `POST /loans?action=registerAjax` (Nuevo préstamo)
*   `POST /loans?action=return` (Devolución)

---

## 📄 Licencia
Este proyecto es desarrollado exclusivamente para propósitos formativos y académicos.

<div align="center">
<a href="https://deepwiki.com/AlssyLop/LibrarySystem"><img src="https://deepwiki.com/badge.svg" alt="Ask DeepWiki"></a>
</div>
