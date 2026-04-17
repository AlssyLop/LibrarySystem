package servlet;

import controller.AuthorController;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet encargado del tráfico HTTP relacionado a Autores
 */
@WebServlet(name = "AuthorServlet", urlPatterns = { "/authors" })
public class AuthorServlet extends HttpServlet {

    private AuthorController authorController = new AuthorController();

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
            java.util.List<model.authorModel> authList = authorController.listAuthorsPaginated(10, 0, query);

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

            request.setAttribute("authors", authorController.listAuthorsPaginated(limit, offset, query));

            int totalRecords = authorController.countAuthors(query);
            int totalPages = (int) Math.ceil((double) totalRecords / limit);

            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("query", query);

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

            authorController.registerAuthor(name, nationality);
            response.sendRedirect("authors");

        } else if ("update".equals(action)) {
            int idAuthor = Integer.parseInt(request.getParameter("idAuthor"));
            String name = request.getParameter("name");
            String nationality = request.getParameter("nationality");

            authorController.updateAuthor(idAuthor, name, nationality);
            response.sendRedirect("authors");
        }
    }
}
