package servlet;

import controller.BookController;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet encargado del tráfico HTTP relacionado a Libros
 */
@WebServlet(name = "BookServlet", urlPatterns = { "/books" })
public class BookServlet extends HttpServlet {

    private BookController bookController = new BookController();

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
            java.util.List<model.bookModel> bList = bookController.listBooksPaginated(10, 0, query);

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
            int limit = 20;
            String pageParam = request.getParameter("page");
            if (pageParam != null && !pageParam.isEmpty()) {
                page = Integer.parseInt(pageParam);
            }
            String query = request.getParameter("query");
            if (query == null)
                query = "";

            int offset = (page - 1) * limit;

            request.setAttribute("books", bookController.listBooksPaginated(limit, offset, query));

            int totalRecords = bookController.countBooks(query);
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

        if ("registerAjax".equals(action)) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            try {
                String title = request.getParameter("title");
                String isbn = request.getParameter("isbn");
                String yearStr = request.getParameter("year");
                String idAuthorStr = request.getParameter("idAuthor");

                if (title == null || title.trim().isEmpty()) {
                    response.getWriter().write("{\"status\":\"error\",\"message\":\"El título es obligatorio.\"}");
                    return;
                }
                if (isbn == null || isbn.trim().isEmpty()) {
                    response.getWriter().write("{\"status\":\"error\",\"message\":\"El ISBN es obligatorio.\"}");
                    return;
                }
                if (idAuthorStr == null || idAuthorStr.isEmpty()) {
                    response.getWriter()
                            .write("{\"status\":\"error\",\"message\":\"Autor no registrado en el sistema\"}");
                    return;
                }

                // Validación ISBN duplicado
                if (bookController.checkIsbnExists(isbn.trim())) {
                    response.getWriter().write("{\"status\":\"error\",\"message\":\"El ISBN ingresado ya existe.\"}");
                    return;
                }

                int year = Integer.parseInt(yearStr);
                int idAuthor = Integer.parseInt(idAuthorStr);

                boolean ok = bookController.registerBook(title, isbn, year, idAuthor);
                if (ok) {
                    response.getWriter()
                            .write("{\"status\":\"success\",\"message\":\"Libro registrado exitosamente.\"}");
                } else {
                    response.getWriter().write("{\"status\":\"error\",\"message\":\"Error al registrar el libro.\"}");
                }
            } catch (Exception e) {
                System.out.println("Error en registerAjax: " + e.getMessage());
                response.getWriter().write("{\"status\":\"error\",\"message\":\"Error interno del servidor: "
                        + e.getMessage().replace("\"", "'") + "\"}");
            }

        } else if ("updateAjax".equals(action)) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            try {
                String idBookStr = request.getParameter("idBook");
                String title = request.getParameter("title");
                String isbn = request.getParameter("isbn");
                String yearStr = request.getParameter("year");
                String idAuthorStr = request.getParameter("idAuthor");

                if (idAuthorStr == null || idAuthorStr.isEmpty()) {
                    response.getWriter()
                            .write("{\"status\":\"error\",\"message\":\"Autor no registrado en el sistema\"}");
                    return;
                }

                int idBook = Integer.parseInt(idBookStr);
                int year = Integer.parseInt(yearStr);
                int idAuthor = Integer.parseInt(idAuthorStr);

                boolean ok = bookController.updateBook(idBook, title, isbn, year, idAuthor);
                if (ok) {
                    response.getWriter()
                            .write("{\"status\":\"success\",\"message\":\"Libro actualizado exitosamente.\"}");
                } else {
                    response.getWriter().write("{\"status\":\"error\",\"message\":\"Error al actualizar el libro.\"}");
                }
            } catch (Exception e) {
                System.out.println("Error en updateAjax: " + e.getMessage());
                response.getWriter().write("{\"status\":\"error\",\"message\":\"Error interno del servidor: "
                        + e.getMessage().replace("\"", "'") + "\"}");
            }

        } else if ("register".equals(action)) {
            String title = request.getParameter("title");
            String isbn = request.getParameter("isbn");
            String yearStr = request.getParameter("year");
            String idAuthorStr = request.getParameter("idAuthor");

            if (yearStr == null || yearStr.isEmpty() || idAuthorStr == null || idAuthorStr.isEmpty()) {
                response.sendRedirect("books");
                return;
            }

            int year = Integer.parseInt(yearStr);
            int idAuthor = Integer.parseInt(idAuthorStr);

            bookController.registerBook(title, isbn, year, idAuthor);
            response.sendRedirect("books");

        } else if ("update".equals(action)) {
            String idBookStr = request.getParameter("idBook");
            String title = request.getParameter("title");
            String isbn = request.getParameter("isbn");
            String yearStr = request.getParameter("year");
            String idAuthorStr = request.getParameter("idAuthor");

            if (idBookStr == null || idBookStr.isEmpty() || yearStr == null || yearStr.isEmpty() || idAuthorStr == null
                    || idAuthorStr.isEmpty()) {
                response.sendRedirect("books");
                return;
            }

            int idBook = Integer.parseInt(idBookStr);
            int year = Integer.parseInt(yearStr);
            int idAuthor = Integer.parseInt(idAuthorStr);

            bookController.updateBook(idBook, title, isbn, year, idAuthor);
            response.sendRedirect("books");
        }
    }
}
