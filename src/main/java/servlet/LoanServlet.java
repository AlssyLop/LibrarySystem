package servlet;

import controller.LoanController;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;

/**
 * Servlet encargado del tráfico HTTP relacionado a los Préstamos
 */
@WebServlet(name = "LoanServlet", urlPatterns = {"/LoanServlet", "/registerloan"})
public class LoanServlet extends HttpServlet {
    
    private LoanController loanController = new LoanController();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String path = request.getServletPath();
        if ("/registerloan".equals(path)) {
            request.getRequestDispatcher("/pages/registerLoan.jsp").forward(request, response);
            return;
        }

        String action = request.getParameter("action");
        
        if ("history".equals(action)) {
            request.setAttribute("loans", loanController.loanHistory());
            request.getRequestDispatcher("/pages/loanHistory.jsp").forward(request, response);
            
        } else if ("register".equals(action)) {
            // Mostrar formulario de registro
            request.getRequestDispatcher("/pages/registerLoan.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String path = request.getServletPath();
        String action = request.getParameter("action");
        
        if ("register".equals(action) || "/registerloan".equals(path)) {
            Date loanDate = Date.valueOf(request.getParameter("loanDate"));
            Date returnDate = Date.valueOf(request.getParameter("returnDate"));
            int idUser = Integer.parseInt(request.getParameter("idUser"));
            int idBook = Integer.parseInt(request.getParameter("idBook"));
            
            loanController.registerLoan(loanDate, returnDate, idUser, idBook);
            response.sendRedirect("LoanServlet?action=history");
            
        } else if ("return".equals(action)) {
            int idLoan = Integer.parseInt(request.getParameter("idLoan"));
            Date returnDate = Date.valueOf(request.getParameter("returnDate"));
            
            loanController.returnBook(idLoan, returnDate);
            response.sendRedirect("LoanServlet?action=history");
        }
    }
}
