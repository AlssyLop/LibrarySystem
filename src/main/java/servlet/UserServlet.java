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
@WebServlet(name = "UserServlet", urlPatterns = {"/UserServlet", "/registeruser"})
public class UserServlet extends HttpServlet {
    
    private UserController userController = new UserController();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String path = request.getServletPath();
        
        // Si el usuario ingresa por la URL principal /registeruser
        if ("/registeruser".equals(path)) {
            // Te dirijo al JSP REAl que creaste en la carpeta pages
            request.getRequestDispatcher("/pages/registerUser.jsp").forward(request, response);
            return;
        }
        
        String action = request.getParameter("action");
        
        if ("list".equals(action)) {
            request.setAttribute("users", userController.listUsers());
            request.getRequestDispatcher("/pages/userList.jsp").forward(request, response);
            
        } else if ("delete".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            userController.deleteUser(id);
            response.sendRedirect("UserServlet?action=list");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String path = request.getServletPath();
        String action = request.getParameter("action");
        
        if ("register".equals(action) || "/registeruser".equals(path)) {
            // Usamos name, email y phone tal cual los definiste en tu HTML
            String name = request.getParameter("name");
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");
            
            userController.registerUser(name, email, phone);
            // Tras registrar, si no tienes userList, podemos reenviarlo a si mismo para probar la web
            response.sendRedirect("registeruser");
        }
    }
}
