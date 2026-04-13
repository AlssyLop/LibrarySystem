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
@WebServlet(name = "AuthorServlet", urlPatterns = {"/AuthorServlet", "/registerAuthor"})
public class AuthorServlet extends HttpServlet {
    
    private AuthorController authorController = new AuthorController();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String path = request.getServletPath();
        if ("/registerAuthor".equals(path)) {
            request.getRequestDispatcher("/pages/registerAuthor.jsp").forward(request, response);
            return;
        }

        String action = request.getParameter("action");
        
        if ("list".equals(action)) {
            request.setAttribute("authors", authorController.listAuthors());
            request.getRequestDispatcher("/pages/authorList.jsp").forward(request, response);
            
        } else if ("register".equals(action)) {
            // Mostrar formulario de registro
            request.getRequestDispatcher("/pages/registerAuthor.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String path = request.getServletPath();
        String action = request.getParameter("action");
        
        if ("register".equals(action) || "/registerAuthor".equals(path)) {
            String name = request.getParameter("name");
            String nationality = request.getParameter("nationality");
            
            authorController.registerAuthor(name, nationality);
            response.sendRedirect("AuthorServlet?action=list");
        }
    }
}
