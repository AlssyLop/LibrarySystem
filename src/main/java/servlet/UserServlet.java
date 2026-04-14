package servlet;

import controller.UserController;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet encargado del tráfico HTTP relacionado a los Usuarios
 */
@WebServlet(name = "UserServlet", urlPatterns = {"/users"})
public class UserServlet extends HttpServlet {
    
    private UserController userController = new UserController();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }
        
        if ("apiSearch".equals(action)) {
            String query = request.getParameter("query");
            if (query == null) query = "";
            java.util.List<model.userModel> usersList = userController.listUsersPaginated(10, 0, query);
            
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < usersList.size(); i++) {
                model.userModel u = usersList.get(i);
                json.append("{\"id\":").append(u.getIdUser()).append(",\"text\":\"").append(u.getIdUser()).append(" - ").append(u.getName().replace("\"", "\\\"")).append("\"}");
                if (i < usersList.size() - 1) json.append(",");
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
            if (query == null) query = "";

            int offset = (page - 1) * limit;
            
            request.setAttribute("users", userController.listUsersPaginated(limit, offset, query));
            
            int totalRecords = userController.countUsers(query);
            int totalPages = (int) Math.ceil((double) totalRecords / limit);
            
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("query", query);
            
            request.getRequestDispatcher("/pages/users.jsp").forward(request, response);
            
        } else if ("delete".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            userController.deleteUser(id);
            response.sendRedirect("users");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if ("register".equals(action)) {
            String name = request.getParameter("name");
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");
            
            userController.registerUser(name, email, phone);
            response.sendRedirect("users");
            
        } else if ("update".equals(action)) {
            int idUser = Integer.parseInt(request.getParameter("idUser"));
            String name = request.getParameter("name");
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");
            
            userController.updateUser(idUser, name, email, phone);
            response.sendRedirect("users");
        }
    }
}
