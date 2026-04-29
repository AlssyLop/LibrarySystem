package controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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

    private ObjectMapper mapper = new ObjectMapper();

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
            List<Map<String, Object>> responseList = bList.stream()
                    .map(b -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", b.getIdBook());
                        map.put("text", b.getIdBook() + " - " + b.getTitle());
                        return map;
                    }).collect(Collectors.toList());
            response.getWriter().write(mapper.writeValueAsString(responseList));
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
            Map<String, String> responseData = new HashMap<>();
            responseData.put("status", "success");
            responseData.put("message", "Libro registrado exitosamente.");
            response.getWriter().write(mapper.writeValueAsString(responseData));
        } catch (Exception e) {
            Map<String, String> responseData = new HashMap<>();
            responseData.put("status", "error");
            responseData.put("message", e.getMessage());
            response.getWriter().write(mapper.writeValueAsString(responseData));
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
            Map<String, String> responseData = new HashMap<>();
            responseData.put("status", "success");
            responseData.put("message", "Libro actualizado exitosamente.");
            response.getWriter().write(mapper.writeValueAsString(responseData));
        } catch (Exception e) {
            if (e.getMessage().equals("No se detectaron cambios.")) {
                Map<String, String> responseData = new HashMap<>();
            responseData.put("status", "info");
            responseData.put("message", "No se detectaron cambios.");
            response.getWriter().write(mapper.writeValueAsString(responseData));
            } else {
                Map<String, String> responseData = new HashMap<>();
            responseData.put("status", "error");
            responseData.put("message", e.getMessage());
            response.getWriter().write(mapper.writeValueAsString(responseData));
            }
        }
    }
}
