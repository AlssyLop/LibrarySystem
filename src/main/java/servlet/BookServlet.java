package servlet;

import controller.BookController;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet encargado del tráfico HTTP relacionado a Libros
 */
@WebServlet(name = "BookServlet", urlPatterns = {"/BookServlet", "/registerbook"})
public class BookServlet extends HttpServlet {
    
    private BookController bookController = new BookController();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String path = request.getServletPath();
        if ("/registerbook".equals(path)) {
            request.getRequestDispatcher("/pages/registerBook.jsp").forward(request, response);
            return;
        }

        String action = request.getParameter("action");
        
        if ("list".equals(action)) {
            request.setAttribute("books", bookController.listBooks());
            request.getRequestDispatcher("/pages/bookList.jsp").forward(request, response);
            
        } else if ("search".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            request.setAttribute("book", bookController.searchBook(id));
            request.getRequestDispatcher("/pages/bookDetail.jsp").forward(request, response);
            
        } else if ("register".equals(action)) {
            // Sirve la vista del formulario HTML si entras por GET a la URL ?action=register
            request.getRequestDispatcher("/pages/registerBook.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String path = request.getServletPath();
        String action = request.getParameter("action");
        
        if ("register".equals(action) || "/registerbook".equals(path)) {
            String title = request.getParameter("title");
            String isbn = request.getParameter("isbn");
            int year = Integer.parseInt(request.getParameter("year"));
            int idAuthor = Integer.parseInt(request.getParameter("idAuthor"));
            
            bookController.registerBook(title, isbn, year, idAuthor);
            response.sendRedirect("BookServlet?action=list");
        }
    }
}
