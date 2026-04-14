package servlet;

import controller.AuthorController;
import controller.BookController;
import controller.LoanController;
import controller.UserController;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard"})
public class DashboardServlet extends HttpServlet {

    private UserController userController = new UserController();
    private BookController bookController = new BookController();
    private AuthorController authorController = new AuthorController();
    private LoanController loanController = new LoanController();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Contamos totales simples usando el query vacio en los metodos count
        int totalUsers = userController.countUsers("");
        int totalBooks = bookController.countBooks("");
        int totalAuthors = authorController.countAuthors("");
        int activeLoans = loanController.listActiveLoans().size();
        int totalLoans = loanController.loanHistory().size();

        request.setAttribute("totalUsers", totalUsers);
        request.setAttribute("totalBooks", totalBooks);
        request.setAttribute("totalAuthors", totalAuthors);
        request.setAttribute("activeLoans", activeLoans);
        request.setAttribute("totalLoans", totalLoans);

        request.getRequestDispatcher("/pages/dashboard.jsp").forward(request, response);
    }
}
