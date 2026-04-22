package controller;

import dao.IUserDAO;
import dao.impl.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.userModel;

import java.io.IOException;

/**
 * Servlet encargado del tráfico HTTP relacionado a los Usuarios
 */
@WebServlet(name = "UserServlet", urlPatterns = { "/users" })
public class UserController extends HttpServlet {

    private IUserDAO userDAO = new UserDAO();

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
            java.util.List<model.userModel> usersList = this.userDAO.listUsersPaginated(10, 0, query);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < usersList.size(); i++) {
                model.userModel u = usersList.get(i);
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
                page = Integer.parseInt(pageParam);
            }
            String query = request.getParameter("query");
            if (query == null)
                query = "";

            int offset = (page - 1) * limit;

            request.setAttribute("users", this.userDAO.listUsersPaginated(limit, offset, query));

            int totalRecords = this.userDAO.countUsers(query);
            int totalPages = (int) Math.ceil((double) totalRecords / limit);

            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("query", query);

            request.getRequestDispatcher("/pages/users.jsp").forward(request, response);

        } else if ("delete".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            this.userDAO.deleteUser(id);
            response.sendRedirect("users");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("register".equals(action)) {
            try {
                String[] validated = validateUser(request.getParameter("name"), request.getParameter("email"), request.getParameter("phone"));
                userModel newUser = new userModel(0, validated[0], validated[1], validated[2]);
                this.userDAO.registerUser(newUser);
            } catch (Exception e) {
                System.out.println("Error validando usuario en registro: " + e.getMessage());
            }
            response.sendRedirect("users");

        } else if ("update".equals(action)) {
            try {
                int idUser = Integer.parseInt(request.getParameter("idUser"));
                String[] validated = validateUser(request.getParameter("name"), request.getParameter("email"), request.getParameter("phone"));
                userModel user = new userModel(idUser, validated[0], validated[1], validated[2], true);
                this.userDAO.updateUser(user);
            } catch (Exception e) {
                System.out.println("Error validando usuario en actualización: " + e.getMessage());
            }
            response.sendRedirect("users");
        }
    }

    private String[] validateUser(String name, String email, String phone) throws Exception {
        // --- REQUERIMIENTO NAME ---
        if (name == null) throw new Exception("El nombre es requerido.");
        name = name.trim().toUpperCase();
        if (name.length() < 3 || name.length() > 100) {
            throw new Exception("El nombre debe tener entre 3 y 100 caracteres.");
        }
        if (!name.matches("^[A-ZÁÉÍÓÚÑ][A-ZÁÉÍÓÚÑ_\\.\\-\\s]*$")) {
            throw new Exception("El nombre contiene caracteres inválidos o no empieza con letra.");
        }

        // --- REQUERIMIENTO EMAIL ---
        if (email == null) throw new Exception("El email es requerido.");
        email = email.trim().toLowerCase();
        if (email.length() > 100) {
            throw new Exception("El email supera el máximo de 100 caracteres.");
        }
        String[] emailParts = email.split("@");
        if (emailParts.length != 2 || emailParts[0].length() > 50 || emailParts[1].length() > 50) {
            throw new Exception("El email debe tener un arroba y partes menores a 50 caracteres.");
        }
        if (!email.matches("^[a-z0-9_\\.\\-]+@[a-z0-9_\\.\\-]+\\.[a-z]{2,}$")) {
            throw new Exception("Formato de email inválido.");
        }

        // --- REQUERIMIENTO PHONE ---
        if (phone == null) throw new Exception("El teléfono es requerido.");
        phone = phone.trim();
        boolean hasPlus = phone.startsWith("+");
        phone = phone.replaceAll("[^0-9]", ""); // Eliminar no numéricos
        if (hasPlus) {
            phone = "+" + phone;
        }
        if (phone.length() < 10 || phone.length() > 15) {
            throw new Exception("El teléfono debe tener entre 10 y 15 caracteres.");
        }

        return new String[]{name, email, phone};
    }
}
