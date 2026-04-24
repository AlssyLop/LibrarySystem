package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import dao.IAuthorDAO;
import dao.impl.AuthorDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.authorModel;

/**
 * Servlet encargado del tráfico HTTP relacionado a Autores
 */
@WebServlet(name = "AuthorServlet", urlPatterns = { "/authors" })
public class AuthorController extends HttpServlet {

    private IAuthorDAO authorDAO = new AuthorDAO();

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
            java.util.List<model.authorModel> authList = this.authorDAO.listAuthorsPaginated(10, 0, query);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < authList.size(); i++) {
                model.authorModel a = authList.get(i);
                json.append("{\"id\":").append(a.getIdAuthor()).append(",\"text\":\"").append(a.getIdAuthor())
                        .append(" - ").append(a.getName().replace("\"", "\\\"")).append("\"}");
                if (i < authList.size() - 1)
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

            request.setAttribute("authors", this.authorDAO.listAuthorsPaginated(limit, offset, query));

            int totalRecords = this.authorDAO.countAuthors(query);
            int totalPages = (int) Math.ceil((double) totalRecords / limit);

            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("query", query);

            // Generar listado de nacionalidades
            ArrayList<String> nacionalidades = nationalityList();
            Collections.sort(nacionalidades);
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
            authorModel author = validateAuthor(true, request.getParameter("idAuthor"),
                    request.getParameter("name"), request.getParameter("nationality"));
            boolean ok = this.authorDAO.registerAuthor(author);
            if (ok) {
                response.getWriter().write("{\"status\":\"success\",\"message\":\"Autor registrado exitosamente.\"}");
            } else {
                response.getWriter().write("{\"status\":\"error\",\"message\":\"Error al registrar el autor.\"}");
            }
        } catch (Exception e) {
            response.getWriter().write("{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}");
        }
    }

    private void updateAuthor(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            authorModel validatedAuthor = validateAuthor(false, request.getParameter("idAuthor"),
                    request.getParameter("name"), request.getParameter("nationality"));

            // Obtener autor actual de la DB para comparar
            authorModel currentAuthor = authorDAO.searchAuthor(validatedAuthor.getIdAuthor());

            // Construir mapa con solo los campos que cambiaron
            Map<String, Object> changes = new HashMap<>();
            if (!currentAuthor.getName().equals(validatedAuthor.getName())) {
                changes.put("name", validatedAuthor.getName());
            }
            if (!currentAuthor.getNationality().equals(validatedAuthor.getNationality())) {
                changes.put("nationality", validatedAuthor.getNationality());
            }

            if (changes.isEmpty()) {
                response.getWriter().write("{\"status\":\"info\",\"message\":\"No se detectaron cambios.\"}");
                return;
            }

            boolean ok = authorDAO.updateAuthorPartial(validatedAuthor.getIdAuthor(), changes);
            if (ok) {
                response.getWriter().write("{\"status\":\"success\",\"message\":\"Autor actualizado exitosamente.\"}");
            } else {
                response.getWriter().write("{\"status\":\"error\",\"message\":\"Error al actualizar el autor.\"}");
            }
        } catch (Exception e) {
            response.getWriter().write("{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}");
        }
    }

    private authorModel validateAuthor(boolean isRegister, String idAuthor, String name, String nationality)
            throws Exception {

        String message = "";
        try {
            // --- ID ---
            int IDautor = 0;
            if (!isRegister) {
                message = "El autor es requerido.";
                messageException(idAuthor == null || idAuthor.trim().isEmpty(), message);
                idAuthor = idAuthor.replaceAll("\\s+", "");
                message = "Autor inválido";
                IDautor = Integer.parseInt(idAuthor);
                messageException(IDautor < 1, message);
                messageException(!this.authorDAO.checkIdAuthorExits(IDautor),
                        "Autor no válido. No se encuentra registrado");
            }

            // --- NAME ---
            message = "El nombre es requerido.";
            messageException(name == null || name.trim().isEmpty(), message);
            name = String.join(" ", name.trim().split("\\s+")).toUpperCase();
            messageException(!name.matches("^[\\p{L}][\\p{L} .'-]*$"),
                    "Nombre inválido");
            messageException(name.length() < 3 || name.length() > 100,
                    "El nombre debe tener entre 3 y 100 caracteres.");

            // --- NATIONALITY ---
            message = "Nacionalidad requerida.";
            messageException(nationality == null || nationality.trim().isEmpty(), message);
            boolean validNationality = nationalityList().stream()
                    .anyMatch(n -> n.equalsIgnoreCase(nationality.trim()));
            messageException(!validNationality, "Nacionalidad inválida.");

            return new authorModel(IDautor, name, nationality.trim().toUpperCase());

        } catch (NullPointerException e) {
            throw new Exception(message);
        } catch (NumberFormatException e) {
            throw new Exception(message);
        }
    }

    private ArrayList<String> nationalityList() {
        String[] paisesCodigos = Locale.getISOCountries();
        ArrayList<String> nacionalidades = new ArrayList<>();
        for (String codigo : paisesCodigos) {
            Locale locale = new Locale("", codigo);
            nacionalidades.add(locale.getDisplayCountry(new Locale("es", "ES")));
        }
        return nacionalidades;
    }

    private void messageException(boolean condition, String message) throws Exception {
        if (condition)
            throw new Exception(message);
    }
}
