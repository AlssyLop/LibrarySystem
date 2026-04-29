package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import service.AuthorService;
import service.BookService;
import service.LoanService;
import service.UserService;

@WebServlet(name = "DashboardServlet", urlPatterns = { "/dashboard" })
public class DashboardController extends HttpServlet {

    private UserService userService = new UserService();
    private LoanService loanService = new LoanService();
    private AuthorService authorService = new AuthorService();
    private BookService bookService = new BookService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Contamos totales de forma eficiente usando los servicios
        int totalUsers = userService.countUsers("");
        int totalBooks = bookService.countBooks("");
        int totalAuthors = authorService.countAuthors("");
        int activeLoans = loanService.countActiveLoans(null, null);
        int totalLoans = loanService.countHistoryLoans(null, null);

        request.setAttribute("totalUsers", totalUsers);
        request.setAttribute("totalBooks", totalBooks);
        request.setAttribute("totalAuthors", totalAuthors);
        request.setAttribute("activeLoans", activeLoans);
        request.setAttribute("totalLoans", totalLoans);

        request.getRequestDispatcher("/pages/dashboard.jsp").forward(request, response);
    }
}
