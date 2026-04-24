<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <!DOCTYPE html>
    <html lang="es">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Test Endpoints</title>
        <style>
            * {
                box-sizing: border-box;
                margin: 0;
                padding: 0;
            }

            body {
                font-family: system-ui, sans-serif;
                background: #0f1117;
                color: #e1e1e6;
                padding: 2rem;
            }

            h1 {
                text-align: center;
                margin-bottom: 2rem;
                color: #a78bfa;
            }

            .grid {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
                gap: 1.5rem;
                max-width: 1400px;
                margin: 0 auto;
            }

            .card {
                background: #1a1b23;
                border: 1px solid #2a2b35;
                border-radius: 12px;
                padding: 1.5rem;
            }

            .card h2 {
                font-size: 1.1rem;
                margin-bottom: 1rem;
                padding-bottom: 0.5rem;
                border-bottom: 1px solid #2a2b35;
            }

            .card h2.users {
                color: #60a5fa;
            }

            .card h2.authors {
                color: #f59e0b;
            }

            .card h2.books {
                color: #34d399;
            }

            .card h2.loans {
                color: #f472b6;
            }

            .form-group {
                margin-bottom: 0.75rem;
            }

            label {
                display: block;
                font-size: 0.8rem;
                color: #9ca3af;
                margin-bottom: 0.25rem;
            }

            input {
                width: 100%;
                padding: 0.5rem 0.75rem;
                background: #0f1117;
                border: 1px solid #2a2b35;
                border-radius: 6px;
                color: #e1e1e6;
                font-size: 0.9rem;
            }

            input:focus {
                outline: none;
                border-color: #a78bfa;
            }

            button {
                width: 100%;
                padding: 0.6rem;
                border: none;
                border-radius: 6px;
                font-size: 0.9rem;
                font-weight: 600;
                cursor: pointer;
                margin-top: 0.5rem;
                transition: opacity 0.2s;
            }

            button:hover {
                opacity: 0.85;
            }

            .btn-users {
                background: #60a5fa;
                color: #000;
            }

            .btn-authors {
                background: #f59e0b;
                color: #000;
            }

            .btn-books {
                background: #34d399;
                color: #000;
            }

            .btn-loans {
                background: #f472b6;
                color: #000;
            }
        </style>
    </head>

    <body>
        <h1>Test Endpoints</h1>

        <div class="grid">
            <!-- USERS -->
            <div class="card">
                <h2 class="users">Registrar Usuario</h2>
                <form id="formUser" onsubmit="return sendForm(event, '/users')">
                    <input type="hidden" name="action" value="register">
                    <div class="form-group">
                        <label>Nombre</label>
                        <input type="text" name="name">
                    </div>
                    <div class="form-group">
                        <label>Email</label>
                        <input type="text" name="email">
                    </div>
                    <div class="form-group">
                        <label>Teléfono</label>
                        <input type="text" name="phone">
                    </div>
                    <button type="submit" class="btn-users">Registrar</button>
                </form>
            </div>

            <div class="card">
                <h2 class="users">Actualizar Usuario</h2>
                <form id="formUserUpdate" onsubmit="return sendForm(event, '/users')">
                    <input type="hidden" name="action" value="update">
                    <div class="form-group">
                        <label>ID Usuario</label>
                        <input type="text" name="idUser">
                    </div>
                    <div class="form-group">
                        <label>Nombre</label>
                        <input type="text" name="name">
                    </div>
                    <div class="form-group">
                        <label>Email</label>
                        <input type="text" name="email">
                    </div>
                    <div class="form-group">
                        <label>Teléfono</label>
                        <input type="text" name="phone">
                    </div>
                    <button type="submit" class="btn-users">Actualizar</button>
                </form>
            </div>

            <!-- AUTHORS -->
            <div class="card">
                <h2 class="authors">Registrar Autor</h2>
                <form id="formAuthor" onsubmit="return sendForm(event, '/authors')">
                    <input type="hidden" name="action" value="register">
                    <div class="form-group">
                        <label>Nombre</label>
                        <input type="text" name="name">
                    </div>
                    <div class="form-group">
                        <label>Nacionalidad</label>
                        <input type="text" name="nationality">
                    </div>
                    <button type="submit" class="btn-authors">Registrar</button>
                </form>
            </div>

            <div class="card">
                <h2 class="authors">Actualizar Autor</h2>
                <form id="formAuthorUpdate" onsubmit="return sendForm(event, '/authors')">
                    <input type="hidden" name="action" value="update">
                    <div class="form-group">
                        <label>ID Autor</label>
                        <input type="text" name="idAuthor">
                    </div>
                    <div class="form-group">
                        <label>Nombre</label>
                        <input type="text" name="name">
                    </div>
                    <div class="form-group">
                        <label>Nacionalidad</label>
                        <input type="text" name="nationality">
                    </div>
                    <button type="submit" class="btn-authors">Actualizar</button>
                </form>
            </div>

            <!-- BOOKS -->
            <div class="card">
                <h2 class="books">Registrar Libro</h2>
                <form id="formBook" onsubmit="return sendForm(event, '/books')">
                    <input type="hidden" name="action" value="register">
                    <div class="form-group">
                        <label>Título</label>
                        <input type="text" name="title">
                    </div>
                    <div class="form-group">
                        <label>ISBN</label>
                        <input type="text" name="isbn">
                    </div>
                    <div class="form-group">
                        <label>Año</label>
                        <input type="text" name="year">
                    </div>
                    <div class="form-group">
                        <label>ID Autor</label>
                        <input type="text" name="idAuthor">
                    </div>
                    <button type="submit" class="btn-books">Registrar</button>
                </form>
            </div>

            <div class="card">
                <h2 class="books">Actualizar Libro</h2>
                <form id="formBookUpdate" onsubmit="return sendForm(event, '/books')">
                    <input type="hidden" name="action" value="update">
                    <div class="form-group">
                        <label>ID Libro</label>
                        <input type="text" name="idBook">
                    </div>
                    <div class="form-group">
                        <label>Título</label>
                        <input type="text" name="title">
                    </div>
                    <div class="form-group">
                        <label>ISBN</label>
                        <input type="text" name="isbn">
                    </div>
                    <div class="form-group">
                        <label>Año</label>
                        <input type="text" name="year">
                    </div>
                    <div class="form-group">
                        <label>ID Autor</label>
                        <input type="text" name="idAuthor">
                    </div>
                    <button type="submit" class="btn-books">Actualizar</button>
                </form>
            </div>

            <!-- LOANS -->
            <div class="card">
                <h2 class="loans">Registrar Préstamo</h2>
                <form id="formLoan" onsubmit="return sendForm(event, '/loans')">
                    <input type="hidden" name="action" value="registerAjax">
                    <div class="form-group">
                        <label>ID Usuario</label>
                        <input type="text" name="idUser">
                    </div>
                    <div class="form-group">
                        <label>ID Libro</label>
                        <input type="text" name="idBook">
                    </div>
                    <button type="submit" class="btn-loans">Registrar</button>
                </form>
            </div>
        </div>

        <script>
            function sendForm(event, url) {
                event.preventDefault();
                const form = event.target;
                const formData = new FormData(form);
                const params = new URLSearchParams(formData).toString();

                fetch(url, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: params
                })
                    .then(res => res.text())
                    .then(text => {
                        try {
                            const data = JSON.parse(text);
                            alert('[' + data.status.toUpperCase() + '] ' + data.message);
                        } catch (e) {
                            alert('Inténtalo más tarde');
                        }
                    })
                    .catch(err => alert('Error de conexión: ' + err.message));

                return false;
            }
        </script>
    </body>

    </html>