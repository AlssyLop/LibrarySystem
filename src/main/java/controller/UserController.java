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
import model.UserModel;
import service.UserService;

/**
 * Servlet encargado del tráfico HTTP relacionado a los Usuarios
 */
@WebServlet(name = "UserServlet", urlPatterns = { "/users" })
public class UserController extends HttpServlet {

    private ObjectMapper mapper = new ObjectMapper();

    private UserService userService = new UserService();

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
            java.util.List<model.UserModel> usersList = this.userService.listUsersPaginated(10, 0, query);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            List<Map<String, Object>> responseList = usersList.stream()
                    .map(u -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", u.getIdUser());
                        map.put("text", u.getIdUser() + " - " + u.getName());
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

            request.setAttribute("users", this.userService.listUsersPaginated(limit, offset, query));

            int totalRecords = this.userService.countUsers(query);
            int totalPages = (int) Math.ceil((double) totalRecords / limit);

            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("query", query);

            request.getRequestDispatcher("/pages/users.jsp").forward(request, response);

        } else if ("delete".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            this.userService.deleteUser(id);
            response.sendRedirect("users");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        if ("register".equals(action)) {
            registerUser(request, response);
        } else if ("update".equals(action)) {
            updateUser(request, response);
        }
    }

    private void registerUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            userService.registerUser(
                    request.getParameter("idUser"),
                    request.getParameter("name"), 
                    request.getParameter("email"),
                    request.getParameter("phone"));
            Map<String, String> responseData = new HashMap<>();
            responseData.put("status", "success");
            responseData.put("message", "Usuario registrado exitosamente.");
            response.getWriter().write(mapper.writeValueAsString(responseData));
        } catch (Exception e) {
            Map<String, String> responseData = new HashMap<>();
            responseData.put("status", "error");
            responseData.put("message", e.getMessage());
            response.getWriter().write(mapper.writeValueAsString(responseData));
        }
    }

    private void updateUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            userService.updateUser(
                    request.getParameter("idUser"),
                    request.getParameter("name"), 
                    request.getParameter("email"),
                    request.getParameter("phone"));
            Map<String, String> responseData = new HashMap<>();
            responseData.put("status", "success");
            responseData.put("message", "Usuario actualizado exitosamente.");
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
