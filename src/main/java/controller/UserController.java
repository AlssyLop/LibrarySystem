package controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import dao.IUserDAO;
import dao.impl.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.userModel;

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
            registerUser(request, response);
        } else if ("update".equals(action)) {
            updateUser(request, response);
        }
    }

    private void registerUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            userModel user = validateUser(true, request.getParameter("idUser"),
                    request.getParameter("name"), request.getParameter("email"),
                    request.getParameter("phone"));
            boolean ok = this.userDAO.registerUser(user);
            if (ok) {
                response.getWriter().write("{\"status\":\"success\",\"message\":\"Usuario registrado exitosamente.\"}");
            } else {
                response.getWriter().write("{\"status\":\"error\",\"message\":\"Error al registrar el usuario.\"}");
            }
        } catch (Exception e) {
            response.getWriter().write("{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}");
        }
    }

    private void updateUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            userModel validatedUser = validateUser(false, request.getParameter("idUser"),
                    request.getParameter("name"), request.getParameter("email"),
                    request.getParameter("phone"));

            // Obtener usuario actual de la DB para comparar
            userModel currentUser = userDAO.searchUser(validatedUser.getIdUser());

            // Construir mapa con solo los campos que cambiaron
            Map<String, Object> changes = new HashMap<>();
            if (!currentUser.getName().equals(validatedUser.getName())) {
                changes.put("name", validatedUser.getName());
            }
            if (!currentUser.getEmail().equals(validatedUser.getEmail())) {
                changes.put("email", validatedUser.getEmail());
            }
            if (!currentUser.getPhone().equals(validatedUser.getPhone())) {
                changes.put("phone", validatedUser.getPhone());
            }

            if (changes.isEmpty()) {
                response.getWriter().write("{\"status\":\"info\",\"message\":\"No se detectaron cambios.\"}");
                return;
            }

            boolean ok = userDAO.updateUserPartial(validatedUser.getIdUser(), changes);
            if (ok) {
                response.getWriter().write("{\"status\":\"success\",\"message\":\"Usuario actualizado exitosamente.\"}");
            } else {
                response.getWriter().write("{\"status\":\"error\",\"message\":\"Error al actualizar el usuario.\"}");
            }
        } catch (Exception e) {
            response.getWriter().write("{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}");
        }
    }

    private userModel validateUser(boolean isRegister, String idUser, String name, String email, String phone)
            throws Exception {

        String message = "";
        try {
            // --- ID ---
            int IDuser = 0;
            if (!isRegister) {
                message = "El usuario es requerido.";
                messageException(idUser == null || idUser.trim().isEmpty(), message);
                idUser = idUser.replaceAll("\\s+", "");
                message = "Usuario inválido";
                IDuser = Integer.parseInt(idUser);
                messageException(IDuser < 1, message);
                messageException(!this.userDAO.checkIdUserExists(IDuser),
                        "Usuario no válido. No se encuentra registrado");
            }

            // --- NAME ---
            message = "El nombre es requerido.";
            messageException(name == null || name.trim().isEmpty(), message);
            name = String.join(" ", name.trim().split("\\s+")).toUpperCase();
            messageException(name.length() < 3 || name.length() > 100,
                    "El nombre debe tener entre 3 y 100 caracteres.");
            messageException(!name.matches("^[A-ZÁÉÍÓÚÑ][A-ZÁÉÍÓÚÑ_\\.\\-\\s]*$"),
                    "Nombre inválido");

            // --- EMAIL ---
            message = "Email requerido.";
            messageException(email == null || email.trim().isEmpty(), message);
            email = email.trim().toLowerCase();
            messageException(email.length() > 100,
                    "Email inválido. Supera el máximo de 100 caracteres.");
            String[] emailParts = email.split("@");
            messageException(emailParts.length != 2 || emailParts[0].length() > 50 || emailParts[1].length() > 50,
                    "Email inválido.");
            messageException(!email.matches("^[a-z0-9_\\.\\-]+@[a-z0-9_\\.\\-]+\\.[a-z]{2,}$"),
                    "Email inválido.");
            // Verificar duplicado de email
            if (isRegister) {
                messageException(this.userDAO.checkEmailExists(email),
                        "El email ya se encuentra registrado.");
            } else {
                userModel currentUser = this.userDAO.searchUser(IDuser);
                if (!currentUser.getEmail().equals(email)) {
                    messageException(this.userDAO.checkEmailExists(email),
                            "El email ya se encuentra asignado a otro usuario.");
                }
            }

            // --- PHONE ---
            message = "Teléfono requerido.";
            messageException(phone == null || phone.trim().isEmpty(), message);
            phone = phone.trim();
            boolean hasPlus = phone.startsWith("+");
            phone = phone.replaceAll("[^0-9]", "");
            if (hasPlus) {
                phone = "+" + phone;
            }
            messageException(phone.length() < 10 || phone.length() > 15,
                    "Teléfono inválido. Debe tener entre 10 y 15 caracteres.");

            return new userModel(IDuser, name, email, phone);

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
