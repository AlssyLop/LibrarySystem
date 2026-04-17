# LibrarySystem 📚

LibrarySystem es un sistema de gestión de bibliotecas para entornos académicos. Desarrollado con **Jakarta EE 10** y diseñado para **Eclipse GlassFish 7**, permite la administración eficiente de usuarios, autores, libros y préstamos mediante una interfaz web moderna, responsiva y optimizada para operar con grandes volúmenes de datos.

---

## 🛠️ Referencia Técnica

Esta sección detalla los componentes técnicos y la arquitectura principal del sistema, sirviendo como mapa general para desarrolladores.

### Stack Tecnológico

| Componente | Tecnología |
|------------|------------|
| **Lenguaje principal** | Java 11 |
| **Plataforma Enterprise**| Jakarta EE 10 (Servlets y plantillas JSP) |
| **Servidor de Apps** | Eclipse GlassFish 7.0.25 |
| **Base de Datos** | MySQL 8.0 |
| **Controlador JDBC** | MySQL Connector/J 9.6.0 |
| **Herramienta de Build**| Apache Maven |
| **Frontend** | HTML5, CSS3 Nativo (Variables), JavaScript Vanilla |
| **Recursos Visuales** | Lucide Icons (vía CDN), Tipografías de Google Fonts |

### Arquitectura de Capas (MVC)

El proyecto mantiene una estricta separación de responsabilidades:

*   **`webapp/pages/` (Vistas):** Documentos JSP reusables para la interfaz gráfica y layouts como modales o cabeceras estáticas.
*   **`servlet/` (Controladores HTTP):** Componentes (`HttpServlet`) destinados a la recepción de peticiones (GET/POST), redirecciones y respuesta de JSON.
*   **`controller/` (Lógica de Negocio):** Actúa como orquestador, evaluando reglas antes de pedir y entregar datos.
*   **`dao/` y `dao/impl/` (Acceso a Datos):** Interfaces de abstracción y sus implementaciones concretas de JDBC usando conectividad controlada por `PreparedStatement`.
*   **`model/` (Entidades):** Objetos de transferencia o de valor que representan tablas únicas (Usuario, Libro, Préstamo).

### Esquema de la Base de Datos Relacional

```text
users          (id_user PK, name, email, phone, activo)
authors        (id_author PK, name, nationality)
books          (id_book PK, title, isbn, year, id_author FK)
loans          (id_loan PK, loan_date, return_date, id_user FK, id_book FK, returned)
```

---

## 🚀 Guía de Instalación y Despliegue (How-to)

Estos pasos explican cómo pasar de un entorno limpio a tener la aplicación completamente funcional en local.

### 1. Prerrequisitos de Entorno
Asegúrate de contar con los siguientes elementos instalados y en el PATH de tu máquina:
- **JDK 11** o superior.
- **Apache Maven 3.8** o superior.
- **MySQL 8.0** o superior.
- **Eclipse GlassFish 7.x** (Configurado con su dominio por defecto, `domain1`).

### 2. Configuración de Base de Datos
Ingresa a tu gestor MySQL (línea de comandos o cliente visual) y crea el esquema:
```sql
CREATE DATABASE DBlibrary;
```
*Atención: Asegúrate de configurar correctamente tus credenciales (usuario y contraseña locales) editando el archivo de conexión correspondiente en `database/ConnectionDB.java` o el respectivo bundle properties.*

### 3. Población de Datos (Opcional)
Si deseas evitar un entorno vacío y probar la eficiencia con miles de datos, ejecuta nuestra simulación:
```bash
python generate_seed.py
mysql -u root -p DBlibrary < seed_data.sql
```
*(Carga un histórico base de 978 usuarios, 1256 autores, 4751 libros y 2791 préstamos).*

### 4. Compilación del Sistema
En la terminal, ubicado en la raíz del proyecto, ejecuta el gestor Maven:
```bash
mvn clean package
```

### 5. Despliegue hacia GlassFish
Mediante el formato *Auto-deploy*:
1. Copia el archivo empaquetado: `target/LibrarySystem-1.0-SNAPSHOT.war`.
2. Pégalo dentro del directorio correspondiente: `glassfish7/glassfish/domains/domain1/autodeploy/`.

*(Alternativa visual: Ingresa desde tu navegador a http://localhost:4848 y súbelo desde el menú de Applications).*

### 6. Acceso al Sistema
Abre un navegador (Chrome o Firefox recomendado) y dirígete a:
```text
http://localhost:8080/LibrarySystem/
```

---

## 📖 Funcionalidades y Optimizaciones (Explicación Contextual)

### 1. Resumen Estadístico (Dashboard)
Es la página inicial del aplicativo. Expone métricas en tiempo real leyendo agregados globales para dar rápida visión operativa a administradores de recintos.

### 2. Listados Inteligentes (Usuarios y Autores)
Para evitar bloqueos al cargar grandes volúmenes de datos en una sola petición web:
*   Se limitó la consulta inicial (ej. 20 resultados).
*   Se incorporó un bloque deslizante dinámico para su paginación (limita visualmente a 5 botones).
*   Las búsquedas operan del lado del servidor SQL (`LIKE %query%`) en lugar de sobrecargar memoria RAM filtrando listas inmensas desde el backend Java.
*   *Nota en Usuarios:* Tienen eliminación "lógica". El usuario es desmarcado a falso mediante el atributo `activo` sin destruir compromisos de préstamos existentes.

### 3. Registro de Libros Anti-Duplicación
El ingreso de nuevos libros usa autocompletado en el front-end con peticiones AJAX, brindando hasta 10 respuestas cortas, evitando que el usuario envíe peticiones masivas enteras. 
Implementa prevención predictiva contra "ISBN clonados", validado usando `SELECT 1 FROM books WHERE isbn=? LIMIT 1`.

### 4. Manejo del Ciclo de Préstamos
Los componentes de préstamos en `/loans` tienen doble pestaña de visualización (Históricos vs Activos). 
Permite filtros combinados que ajustan iterativamente un `StringBuilder` en SQL, haciendo posible buscar de forma ágil desde el identificador de usuario hasta por margen de fechas.

### Endpoints AJAX 
Como referencia de la API reactiva interna que posee el sistema (Devuelve respuestas asíncronas tipo JSON al cliente JavaScript HTML):
*   `GET /users?action=apiSearch&query=...`
*   `GET /authors?action=apiSearch&query=...`
*   `GET /books?action=apiSearch&query=...`
*   `POST /books` + variable body `action=registerAjax` o `action=updateAjax`.
*   `POST /loans` + variable body `action=registerAjax`.

---

## 📄 Licencia
Este proyecto es desarrollado y distribuido exclusivamente para propósitos formativos y académicos.
