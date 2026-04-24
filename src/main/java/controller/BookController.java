package controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Year;
import java.util.HashMap;
import java.util.Map;

import dao.IAuthorDAO;
import dao.IBookDAO;
import dao.impl.AuthorDAO;
import dao.impl.BookDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.bookModel;

/**
 * Servlet encargado del tráfico HTTP relacionado a Libros
 */
@WebServlet(name = "BookServlet", urlPatterns = { "/books" })
public class BookController extends HttpServlet {

    private IBookDAO bookDAO = new BookDAO();
    private IAuthorDAO authorDAO = new AuthorDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        if ("apiSearch".equals(action)) {
            String query = request.getParameter("query");
            if (query == null)
                query = "";
            java.util.List<model.bookModel> bList = this.bookDAO.listBooksPaginated(10, 0, query);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < bList.size(); i++) {
                model.bookModel b = bList.get(i);
                json.append("{\"id\":").append(b.getIdBook()).append(",\"text\":\"").append(b.getIdBook()).append(" - ")
                        .append(b.getTitle().replace("\"", "\\\"")).append("\"}");
                if (i < bList.size() - 1)
                    json.append(",");
            }
            json.append("]");
            response.getWriter().write(json.toString());
            return;
        }

        if ("list".equals(action)) {
            int page = 1;
            int limit = 15;
            String pageParam = request.getParameter("page");
            if (pageParam != null && !pageParam.isEmpty()) {
                try {
                    page = Integer.parseInt(pageParam);
                } catch (NumberFormatException e) {
                    page = 1;
                }
            }
            String query = request.getParameter("query");
            if (query == null)
                query = "";

            int offset = (page - 1) * limit;

            request.setAttribute("books", this.bookDAO.listBooksPaginated(limit, offset, query));

            int totalRecords = this.bookDAO.countBooks(query);
            int totalPages = (int) Math.ceil((double) totalRecords / limit);

            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("query", query);

            request.getRequestDispatcher("/pages/books.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("register".equals(action)) {
            registerBook(request, response);
        } else if ("update".equals(action)) {
            updateBook(request, response);
        }
    }

    private void registerBook(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            bookModel book = validateBook(true,
                    request.getParameter("idBook"),
                    request.getParameter("title"),
                    request.getParameter("isbn"),
                    request.getParameter("year"),
                    request.getParameter("idAuthor"));
            boolean ok = this.bookDAO.registerBook(book);
            if (ok) {
                response.getWriter().write("{\"status\":\"success\",\"message\":\"Libro registrado exitosamente.\"}");
            } else {
                response.getWriter().write("{\"status\":\"error\",\"message\":\"Error al registrar el libro.\"}");
            }
        } catch (Exception e) {
            response.getWriter().write("{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}");
        }
    }

    private void updateBook(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            bookModel validatedBook = validateBook(false,
                    request.getParameter("idBook"),
                    request.getParameter("title"),
                    request.getParameter("isbn"),
                    request.getParameter("year"),
                    request.getParameter("idAuthor"));

            // Obtener libro actual de la DB para comparar
            bookModel currentBook = bookDAO.searchBook(validatedBook.getIdBook());

            // Construir mapa con solo los campos que cambiaron
            Map<String, Object> changes = new HashMap<>();
            if (!currentBook.getTitle().equals(validatedBook.getTitle())) {
                changes.put("title", validatedBook.getTitle());
            }
            if (!currentBook.getIsbn().equals(validatedBook.getIsbn())) {
                changes.put("isbn", validatedBook.getIsbn());
            }
            if (currentBook.getYear() != validatedBook.getYear()) {
                changes.put("year", validatedBook.getYear());
            }
            if (currentBook.getIdAuthor() != validatedBook.getIdAuthor()) {
                changes.put("id_author", validatedBook.getIdAuthor());
            }

            if (changes.isEmpty()) {
                response.getWriter().write("{\"status\":\"info\",\"message\":\"No se detectaron cambios.\"}");
                return;
            }

            boolean ok = bookDAO.updateBookPartial(validatedBook.getIdBook(), changes);
            if (ok) {
                response.getWriter().write("{\"status\":\"success\",\"message\":\"Libro actualizado exitosamente.\"}");
            } else {
                response.getWriter().write("{\"status\":\"error\",\"message\":\"Error al actualizar el libro.\"}");
            }
        } catch (Exception e) {
            response.getWriter().write("{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}");
        }
    }

    private bookModel validateBook(boolean checkIdLibro, String idBook, String title, String isbn, String year,
            String idAuthor)
            throws Exception {

        String message = "";
        try {
            message = "El libro es requerido.";
            idBook = idBook.replaceAll("\\s+", "");
            messageException(idBook.isBlank(), message);
            message = "Libro inválido";
            int IDbook = Integer.parseInt(idBook);
            messageException(IDbook < 1, message);
            if (checkIdLibro)
                messageException(this.bookDAO.checkIdLibroExits(IDbook), "El libro ya existe.");
            else
                messageException(!this.bookDAO.checkIdLibroExits(IDbook),
                        "Libro no válido. No se encuentra resgistrado");

            message = "El titulo es requerido.";
            title = String.join(" ", title.trim().split("\\s+"));
            messageException(title.isBlank(), message);
            messageException(title.length() < 3 || title.length() > 150,
                    "Titulo inválido. debe tener entre 3 y 150 caracteres.");
            String regexTitle = "^[\\p{L}0-9\\s.,!?:;'-]{3,150}$";
            messageException(!title.matches(regexTitle), "Título inválido");

            message = "El ISBN es requerido.";
            isbn = isbn.replaceAll("\\s+", "");
            messageException(isbn.isBlank(), message);
            messageException(isbn.length() < 13 || isbn.length() > 20,
                    "ISBN inválido. Debe tener entre 13 y 20 caracteres.");
            String regexIsbn = "^(?:ISBN(?:-1[03])?:?\\ )?(?=[-0-9]{13}$|[-0-9X]{10}$|[-0-9]{17}$|97[89][-0-9]{13}$)(?:97[89][-0-9]{13}|[0-9]{1,5}[-0-9]+[0-9X])$";
            messageException(!isbn.matches(regexIsbn), "ISBN inválido.");
            if (checkIdLibro) {
                messageException(this.bookDAO.checkIsbnExists(isbn), "El ISBN ya existe.");
            } else {
                String currentIsbn = this.bookDAO.searchBook(IDbook).getIsbn();
                if (!currentIsbn.equals(isbn)) {
                    messageException(this.bookDAO.checkIsbnExists(isbn),
                            "Error, el ISBN ingresado " + isbn + " ya se encuentra asignado.");
                }
            }

            message = "El año de publicación es requerido.";
            year = year.replaceAll("\\s+", "");
            messageException(year.isBlank(), message);
            message = "Año de publicación inválido";
            int Year = Integer.parseInt(year);
            messageException(Year < 1450 || Year > LocalDate.now().getYear(), message);

            message = "El autor es requerido.";
            messageException(idAuthor.isBlank(), message);
            message = "Autor no válido";
            messageException(idAuthor.matches(".*\\s.*"), message);
            int IDautor = Integer.parseInt(idAuthor);
            messageException(IDautor < 1, message);
            messageException(!this.authorDAO.checkIdAuthorExits(IDautor),
                    "Autor no válido. No se encuentra registrado");
            return new bookModel(IDbook, title.toUpperCase(), isbn, Year, IDautor);

        } catch (NullPointerException e) {
            throw new Exception(message);
        } catch (NumberFormatException e) {
            throw new Exception(message);
        }
    }

    private void messageException(boolean condition, String message) throws Exception {
        if (condition)
            throw new Exception(message);
    }

}
