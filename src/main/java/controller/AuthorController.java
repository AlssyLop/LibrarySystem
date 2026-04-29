package controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.util.ArrayList;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.AuthorModel;
import service.AuthorService;

/**
 * Servlet encargado del tráfico HTTP relacionado a Autores
 */
@WebServlet(name = "AuthorServlet", urlPatterns = { "/authors" })
public class AuthorController extends HttpServlet {

    private ObjectMapper mapper = new ObjectMapper();

    private AuthorService authorService = new AuthorService();

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
            java.util.List<model.AuthorModel> authList = this.authorService.listAuthorsPaginated(10, 0, query);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            List<Map<String, Object>> responseList = authList.stream()
                    .map(a -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", a.getIdAuthor());
                        map.put("text", a.getIdAuthor() + " - " + a.getName());
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

            request.setAttribute("authors", this.authorService.listAuthorsPaginated(limit, offset, query));

            int totalRecords = this.authorService.countAuthors(query);
            int totalPages = (int) Math.ceil((double) totalRecords / limit);

            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("query", query);

            // Generar listado de nacionalidades
            ArrayList<String> nacionalidades = authorService.getNationalities();
            request.setAttribute("nationalities", nacionalidades);

            request.getRequestDispatcher("/pages/authors.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        if ("register".equals(action)) {
            registerAuthor(request, response);
        } else if ("update".equals(action)) {
            updateAuthor(request, response);
        }
    }

    private void registerAuthor(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            authorService.registerAuthor(
                    request.getParameter("idAuthor"),
                    request.getParameter("name"), 
                    request.getParameter("nationality"));
            Map<String, String> responseData = new HashMap<>();
            responseData.put("status", "success");
            responseData.put("message", "Autor registrado exitosamente.");
            response.getWriter().write(mapper.writeValueAsString(responseData));
        } catch (Exception e) {
            Map<String, String> responseData = new HashMap<>();
            responseData.put("status", "error");
            responseData.put("message", e.getMessage());
            response.getWriter().write(mapper.writeValueAsString(responseData));
        }
    }

    private void updateAuthor(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            authorService.updateAuthor(
                    request.getParameter("idAuthor"),
                    request.getParameter("name"), 
                    request.getParameter("nationality"));
            Map<String, String> responseData = new HashMap<>();
            responseData.put("status", "success");
            responseData.put("message", "Autor actualizado exitosamente.");
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
