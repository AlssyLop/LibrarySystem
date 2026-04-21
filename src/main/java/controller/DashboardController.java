package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import dao.IAuthorDAO;
import dao.IBookDAO;
import dao.ILoanDAO;
import dao.IUserDAO;
import dao.impl.AuthorDAO;
import dao.impl.BookDAO;
import dao.impl.LoanDAO;
import dao.impl.UserDAO;

@WebServlet(name = "DashboardServlet", urlPatterns = { "/dashboard" })
public class DashboardController extends HttpServlet {

    private IUserDAO userDAO = new UserDAO();
    private ILoanDAO loanDAO = new LoanDAO();
    private IAuthorDAO authorDAO = new AuthorDAO();
    private IBookDAO bookDAO = new BookDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Contamos totales simples usando el query vacio en los metodos count
        int totalUsers = userDAO.countUsers("");
        int totalBooks = bookDAO.countBooks("");
        int totalAuthors = authorDAO.countAuthors("");
        int activeLoans = loanDAO.listActiveLoans().size();
        int totalLoans = loanDAO.loanHistory().size();

        request.setAttribute("totalUsers", totalUsers);
        request.setAttribute("totalBooks", totalBooks);
        request.setAttribute("totalAuthors", totalAuthors);
        request.setAttribute("activeLoans", activeLoans);
        request.setAttribute("totalLoans", totalLoans);

        request.getRequestDispatcher("/pages/dashboard.jsp").forward(request, response);
    }
}
