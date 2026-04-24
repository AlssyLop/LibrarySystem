package controller;

import java.io.IOException;
import java.sql.Date;

import dao.IBookDAO;
import dao.ILoanDAO;
import dao.IUserDAO;
import dao.impl.BookDAO;
import dao.impl.LoanDAO;
import dao.impl.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.loanModel;

/**
 * Servlet encargado del tráfico HTTP relacionado a los Préstamos
 */
@WebServlet(name = "LoanServlet", urlPatterns = { "/loans" })
public class LoanController extends HttpServlet {

    private ILoanDAO loanDAO = new LoanDAO();
    private IUserDAO userDAO = new UserDAO();
    private IBookDAO bookDAO = new BookDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        if ("list".equals(action)) {
            int limit = 15;
            int pageActive = 1;
            int pageHistory = 1;

            String pActive = request.getParameter("pageActive");
            if (pActive != null && !pActive.isEmpty())
                try {
                    pageActive = Integer.parseInt(pActive);
                } catch (NumberFormatException e) {
                    pageActive = 1;
                }

            String pHistory = request.getParameter("pageHistory");
            if (pHistory != null && !pHistory.isEmpty())
                try {
                    pageHistory = Integer.parseInt(pHistory);
                } catch (NumberFormatException e) {
                    pageHistory = 1;
                }

            String activeTab = request.getParameter("tab");
            if (activeTab == null)
                activeTab = "actives";

            // Extract filters for active tab
            Integer idUserSearch = null;
            String userSearchParam = request.getParameter("idUserSearch");
            if (userSearchParam != null && !userSearchParam.trim().isEmpty()) {
                try {
                    idUserSearch = Integer.parseInt(userSearchParam.trim());
                } catch (NumberFormatException e) {
                }
            }

            Date dateFilter = null;
            String dateParam = request.getParameter("dateFilter");
            if (dateParam != null && !dateParam.trim().isEmpty()) {
                try {
                    dateFilter = Date.valueOf(dateParam.trim());
                } catch (IllegalArgumentException e) {
                    dateFilter = null;
                }
            }

            // Extract filters for history tab
            Integer idUserSearchHist = null;
            String userSearchHistParam = request.getParameter("idUserSearchHist");
            if (userSearchHistParam != null && !userSearchHistParam.trim().isEmpty()) {
                try {
                    idUserSearchHist = Integer.parseInt(userSearchHistParam.trim());
                } catch (NumberFormatException e) {
                }
            }

            Date dateFilterHist = null;
            String dateHistParam = request.getParameter("dateFilterHist");
            if (dateHistParam != null && !dateHistParam.trim().isEmpty()) {
                dateFilterHist = Date.valueOf(dateHistParam.trim());
            }

            int offsetActive = (pageActive - 1) * limit;
            int offsetHistory = (pageHistory - 1) * limit;

            request.setAttribute("activeLoans",
                    this.loanDAO.listActiveLoansPaginated(limit, offsetActive, idUserSearch, dateFilter));
            request.setAttribute("allLoans",
                    this.loanDAO.loanHistoryPaginated(limit, offsetHistory, idUserSearchHist, dateFilterHist));

            request.setAttribute("activeCurrentPage", pageActive);
            request.setAttribute("historyCurrentPage", pageHistory);
            request.setAttribute("activeTotalPages",
                    (int) Math.ceil((double) this.loanDAO.countActiveLoans(idUserSearch, dateFilter) / limit));
            request.setAttribute("historyTotalPages",
                    (int) Math.ceil((double) this.loanDAO.countHistoryLoans(idUserSearchHist, dateFilterHist) / limit));
            request.setAttribute("activeTab", activeTab);

            // Preserve search state
            request.setAttribute("idUserSearch", idUserSearch != null ? idUserSearch : "");
            request.setAttribute("dateFilter", dateFilter != null ? dateFilter.toString() : "");
            request.setAttribute("idUserSearchHist", idUserSearchHist != null ? idUserSearchHist : "");
            request.setAttribute("dateFilterHist", dateFilterHist != null ? dateFilterHist.toString() : "");

            request.getRequestDispatcher("/pages/loans.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("registerAjax".equals(action)) {
            registerLoan(request, response);
        }
    }

    private void registerLoan(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            loanModel loan = validateLoan(request.getParameter("idUser"), request.getParameter("idBook"));
            boolean ok = this.loanDAO.registerLoan(loan);
            if (ok) {
                response.getWriter().write("{\"status\":\"success\",\"message\":\"Préstamo registrado exitosamente.\"}");
            } else {
                response.getWriter().write("{\"status\":\"error\",\"message\":\"Error al registrar el préstamo.\"}");
            }
        } catch (Exception e) {
            response.getWriter().write("{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}");
        }
    }

    private loanModel validateLoan(String idUserStr, String idBookStr) throws Exception {
        String message = "";
        try {
            // --- ID USER ---
            message = "Debe seleccionar un usuario.";
            messageException(idUserStr == null || idUserStr.trim().isEmpty(), message);
            idUserStr = idUserStr.replaceAll("\\s+", "");
            message = "Usuario inválido";
            int idUser = Integer.parseInt(idUserStr);
            messageException(idUser < 1, message);
            messageException(!this.userDAO.checkIdUserExists(idUser),
                    "Usuario no válido. No se encuentra registrado.");

            // --- ID BOOK ---
            message = "Debe seleccionar un libro.";
            messageException(idBookStr == null || idBookStr.trim().isEmpty(), message);
            idBookStr = idBookStr.replaceAll("\\s+", "");
            message = "Libro inválido";
            int idBook = Integer.parseInt(idBookStr);
            messageException(idBook < 1, message);
            messageException(!this.bookDAO.checkIdLibroExits(idBook),
                    "Libro no válido. No se encuentra registrado.");

            // --- DUPLICADO ---
            messageException(this.loanDAO.checkActiveLoanExists(idUser, idBook),
                    "El usuario ya tiene un préstamo activo de este libro.");

            Date loanDate = new Date(System.currentTimeMillis());
            return new loanModel(0, loanDate, null, idUser, idBook);

        } catch (NullPointerException e) {
            throw new Exception(message);
        } catch (NumberFormatException e) {
            throw new Exception(message);
        }
    }

    private void messageException(boolean condition, String message) throws Exception {
        if (condition)
            throw new Exception(message);
    }
}
