package controller;

import dao.IBookDAO;
import dao.impl.BookDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.bookModel;

import java.io.IOException;

/**
 * Servlet encargado del tráfico HTTP relacionado a Libros
 */
@WebServlet(name = "BookServlet", urlPatterns = { "/books" })
public class BookController extends HttpServlet {

    private IBookDAO bookDAO = new BookDAO();

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
                page = Integer.parseInt(pageParam);
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

        if ("registerAjax".equals(action)) {
            handleRegisterAjax(request, response);
        } else if ("updateAjax".equals(action)) {
            handleUpdateAjax(request, response);
        } else if ("register".equals(action)) {
            handleRegister(request, response);
        } else if ("update".equals(action)) {
            handleUpdate(request, response);
        }
    }

    private void handleRegisterAjax(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            String title = request.getParameter("title");
            String isbn = request.getParameter("isbn");
            int year = Integer.parseInt(request.getParameter("year"));
            int idAuthor = Integer.parseInt(request.getParameter("idAuthor"));

            if (this.bookDAO.checkIsbnExists(isbn.trim())) {
                response.getWriter().write("{\"status\":\"error\",\"message\":\"El ISBN ingresado ya existe.\"}");
                return;
            }
            bookModel book = new bookModel(0, title, isbn, year, idAuthor);
            boolean ok = this.bookDAO.registerBook(book);
            if (ok) {
                response.getWriter().write("{\"status\":\"success\",\"message\":\"Libro registrado exitosamente.\"}");
            } else {
                response.getWriter().write("{\"status\":\"error\",\"message\":\"Error al registrar el libro.\"}");
            }
        } catch (Exception e) {
            response.getWriter().write("{\"status\":\"error\",\"message\":\"Error interno: " + e.getMessage() + "\"}");
        }
    }

    private void handleUpdateAjax(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            int idBook = Integer.parseInt(request.getParameter("idBook"));
            String title = request.getParameter("title");
            String isbn = request.getParameter("isbn");
            int year = Integer.parseInt(request.getParameter("year"));
            int idAuthor = Integer.parseInt(request.getParameter("idAuthor"));

            bookModel book = new bookModel(idBook, title, isbn, year, idAuthor);
            boolean ok = bookDAO.updateBook(book);
            if (ok) {
                response.getWriter().write("{\"status\":\"success\",\"message\":\"Libro actualizado exitosamente.\"}");
            } else {
                response.getWriter().write("{\"status\":\"error\",\"message\":\"Error al actualizar el libro.\"}");
            }
        } catch (Exception e) {
            response.getWriter().write("{\"status\":\"error\",\"message\":\"Error interno: " + e.getMessage() + "\"}");
        }
    }

    private void handleRegister(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String title = request.getParameter("title");
        String isbn = request.getParameter("isbn");
        int year = Integer.parseInt(request.getParameter("year"));
        int idAuthor = Integer.parseInt(request.getParameter("idAuthor"));

        bookModel newBook = new bookModel(0, title, isbn, year, idAuthor);
        this.bookDAO.registerBook(newBook);

        response.sendRedirect("books");
    }

    private void handleUpdate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int idBook = Integer.parseInt(request.getParameter("idBook"));
        String title = request.getParameter("title");
        String isbn = request.getParameter("isbn");
        int year = Integer.parseInt(request.getParameter("year"));
        int idAuthor = Integer.parseInt(request.getParameter("idAuthor"));

        bookModel book = new bookModel(idBook, title, isbn, year, idAuthor);
        this.bookDAO.updateBook(book);

        response.sendRedirect("books");
    }
}
