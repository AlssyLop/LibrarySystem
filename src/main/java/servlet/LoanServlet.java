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
@WebServlet(name = "LoanServlet", urlPatterns = {"/loans"})
public class LoanServlet extends HttpServlet {
    
    private LoanController loanController = new LoanController();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }
        
        if ("list".equals(action)) {
            int limit = 20;
            int pageActive = 1;
            int pageHistory = 1;

            String pActive = request.getParameter("pageActive");
            if (pActive != null && !pActive.isEmpty()) pageActive = Integer.parseInt(pActive);

            String pHistory = request.getParameter("pageHistory");
            if (pHistory != null && !pHistory.isEmpty()) pageHistory = Integer.parseInt(pHistory);

            String activeTab = request.getParameter("tab");
            if (activeTab == null) activeTab = "actives";

            // Extract filters
            Integer idUserSearch = null;
            String userSearchParam = request.getParameter("idUserSearch");
            if (userSearchParam != null && !userSearchParam.trim().isEmpty()) {
                try {
                    idUserSearch = Integer.parseInt(userSearchParam.trim());
                } catch(NumberFormatException e){}
            }

            Date dateFilter = null;
            String dateParam = request.getParameter("dateFilter");
            if (dateParam != null && !dateParam.trim().isEmpty()) {
                dateFilter = Date.valueOf(dateParam.trim());
            }

            int offsetActive = (pageActive - 1) * limit;
            int offsetHistory = (pageHistory - 1) * limit;

            request.setAttribute("activeLoans", loanController.listActiveLoansPaginated(limit, offsetActive, idUserSearch, dateFilter));
            request.setAttribute("allLoans", loanController.loanHistoryPaginated(limit, offsetHistory));
            
            request.setAttribute("activeCurrentPage", pageActive);
            request.setAttribute("historyCurrentPage", pageHistory);
            request.setAttribute("activeTotalPages", (int) Math.ceil((double) loanController.countActiveLoans(idUserSearch, dateFilter) / limit));
            request.setAttribute("historyTotalPages", (int) Math.ceil((double) loanController.countHistoryLoans() / limit));
            request.setAttribute("activeTab", activeTab);
            
            // Preserve search state
            request.setAttribute("idUserSearch", idUserSearch != null ? idUserSearch : "");
            request.setAttribute("dateFilter", dateFilter != null ? dateFilter.toString() : "");

            request.getRequestDispatcher("/pages/loans.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if ("registerAjax".equals(action)) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            String idUserStr = request.getParameter("idUser");
            String idBookStr = request.getParameter("idBook");
            
            if (idUserStr == null || idUserStr.trim().isEmpty() || idBookStr == null || idBookStr.trim().isEmpty()) {
                response.getWriter().write("{\"status\":\"error\",\"message\":\"Debe seleccionar un usuario y un libro.\"}");
                return;
            }
            
            try {
                int idUser = Integer.parseInt(idUserStr.trim());
                int idBook = Integer.parseInt(idBookStr.trim());
                Date loanDate = new Date(System.currentTimeMillis());
                
                boolean ok = loanController.registerLoan(loanDate, null, idUser, idBook);
                if (ok) {
                    response.getWriter().write("{\"status\":\"success\",\"message\":\"Préstamo registrado exitosamente.\"}");
                } else {
                    response.getWriter().write("{\"status\":\"error\",\"message\":\"Error al registrar el préstamo.\"}");
                }
            } catch (NumberFormatException e) {
                response.getWriter().write("{\"status\":\"error\",\"message\":\"Los IDs de usuario o libro no son válidos.\"}");
            }
            
        } else if ("register".equals(action)) {
            Date loanDate = new Date(System.currentTimeMillis());
            
            String idUserStr = request.getParameter("idUser");
            String idBookStr = request.getParameter("idBook");
            
            if (idUserStr == null || idUserStr.isEmpty() || idBookStr == null || idBookStr.isEmpty()) {
                response.sendRedirect("loans");
                return;
            }
            
            int idUser = Integer.parseInt(idUserStr);
            int idBook = Integer.parseInt(idBookStr);
            
            loanController.registerLoan(loanDate, null, idUser, idBook);
            response.sendRedirect("loans");
            
        } else if ("return".equals(action)) {
            int idLoan = Integer.parseInt(request.getParameter("idLoan"));
            Date returnDate = new Date(System.currentTimeMillis()); // Fecha actual de devolucion
            
            loanController.returnBook(idLoan, returnDate);
            response.sendRedirect("loans");
        }
    }
}
