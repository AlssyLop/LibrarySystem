package controller;

import dao.IAuthorDAO;
import dao.impl.AuthorDAO;
import model.authorModel;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

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
                page = Integer.parseInt(pageParam);
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
            String[] paisesCodigos = Locale.getISOCountries();
            ArrayList<String> nacionalidades = new ArrayList<>();
            for (String codigo : paisesCodigos) {
                Locale locale = new Locale("", codigo);
                nacionalidades.add(locale.getDisplayCountry(new Locale("es", "ES")));
            }
            Collections.sort(nacionalidades);
            request.setAttribute("nationalities", nacionalidades);

            request.getRequestDispatcher("/pages/authors.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("register".equals(action)) {
            String name = request.getParameter("name");
            String nationality = request.getParameter("nationality");

            // Registra un nuevo autor
            authorModel newAuthor = new authorModel(0, name, nationality);
            this.authorDAO.registerAuthor(newAuthor);
            response.sendRedirect("authors");

        } else if ("update".equals(action)) {
            int idAuthor = Integer.parseInt(request.getParameter("idAuthor"));
            String name = request.getParameter("name");
            String nationality = request.getParameter("nationality");

            // Actualizar datos del autor con authorDAO
            authorModel author = new authorModel(idAuthor, name, nationality);
            this.authorDAO.updateAuthor(author);

            // Redireccionar en la vista /authors
            response.sendRedirect("authors");
        }
    }
}
