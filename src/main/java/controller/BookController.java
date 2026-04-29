package controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.BookModel;
import service.BookService;

/**
 * Servlet encargado del tráfico HTTP relacionado a Libros
 */
@WebServlet(name = "BookServlet", urlPatterns = { "/books" })
public class BookController extends HttpServlet {

    private BookService bookService = new BookService();

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
            java.util.List<model.BookModel> bList = this.bookService.listBooksPaginated(10, 0, query);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < bList.size(); i++) {
                model.BookModel b = bList.get(i);
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

            request.setAttribute("books", this.bookService.listBooksPaginated(limit, offset, query));

            int totalRecords = this.bookService.countBooks(query);
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

        request.setCharacterEncoding("UTF-8");
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
            bookService.registerBook(
                    request.getParameter("idBook"),
                    request.getParameter("title"),
                    request.getParameter("isbn"),
                    request.getParameter("year"),
                    request.getParameter("idAuthor"));
            response.getWriter().write("{\"status\":\"success\",\"message\":\"Libro registrado exitosamente.\"}");
        } catch (Exception e) {
            response.getWriter().write("{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}");
        }
    }

    private void updateBook(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            bookService.updateBook(
                    request.getParameter("idBook"),
                    request.getParameter("title"),
                    request.getParameter("isbn"),
                    request.getParameter("year"),
                    request.getParameter("idAuthor"));
            response.getWriter().write("{\"status\":\"success\",\"message\":\"Libro actualizado exitosamente.\"}");
        } catch (Exception e) {
            if (e.getMessage().equals("No se detectaron cambios.")) {
                response.getWriter().write("{\"status\":\"info\",\"message\":\"No se detectaron cambios.\"}");
            } else {
                response.getWriter().write("{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}");
            }
        }
    }
}
