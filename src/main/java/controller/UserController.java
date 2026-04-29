package controller;

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
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < usersList.size(); i++) {
                model.UserModel u = usersList.get(i);
                json.append("{\"id\":").append(u.getIdUser()).append(",\"text\":\"").append(u.getIdUser()).append(" - ")
                        .append(u.getName().replace("\"", "\\\"")).append("\"}");
                if (i < usersList.size() - 1)
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
            response.getWriter().write("{\"status\":\"success\",\"message\":\"Usuario registrado exitosamente.\"}");
        } catch (Exception e) {
            response.getWriter().write("{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}");
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
            response.getWriter().write("{\"status\":\"success\",\"message\":\"Usuario actualizado exitosamente.\"}");
        } catch (Exception e) {
            if (e.getMessage().equals("No se detectaron cambios.")) {
                response.getWriter().write("{\"status\":\"info\",\"message\":\"No se detectaron cambios.\"}");
            } else {
                response.getWriter().write("{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}");
            }
        }
    }
}
