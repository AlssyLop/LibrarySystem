package controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.sql.Date;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.LoanService;

/**
 * Servlet encargado del tráfico HTTP relacionado a los Préstamos
 */
@WebServlet(name = "LoanServlet", urlPatterns = { "/loans" })
public class LoanController extends HttpServlet {

    private ObjectMapper mapper = new ObjectMapper();

    private LoanService loanService = new LoanService();

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
                try {
                    dateFilterHist = Date.valueOf(dateHistParam.trim());
                } catch (IllegalArgumentException e) {
                    dateFilterHist = null;
                }
            }

            int offsetActive = (pageActive - 1) * limit;
            int offsetHistory = (pageHistory - 1) * limit;

            request.setAttribute("activeLoans",
                    this.loanService.listActiveLoansPaginated(limit, offsetActive, idUserSearch, dateFilter));
            request.setAttribute("allLoans",
                    this.loanService.loanHistoryPaginated(limit, offsetHistory, idUserSearchHist, dateFilterHist));

            request.setAttribute("activeCurrentPage", pageActive);
            request.setAttribute("historyCurrentPage", pageHistory);
            request.setAttribute("activeTotalPages",
                    (int) Math.ceil((double) this.loanService.countActiveLoans(idUserSearch, dateFilter) / limit));
            request.setAttribute("historyTotalPages",
                    (int) Math.ceil((double) this.loanService.countHistoryLoans(idUserSearchHist, dateFilterHist) / limit));
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

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        if ("registerAjax".equals(action)) {
            registerLoan(request, response);
        } else if ("return".equals(action)) {
            returnLoan(request, response);
        }
    }

    private void returnLoan(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            int idLoan = Integer.parseInt(request.getParameter("idLoan"));
            loanService.returnBook(idLoan);
            Map<String, String> responseData = new HashMap<>();
            responseData.put("status", "success");
            responseData.put("message", "Libro devuelto exitosamente.");
            response.getWriter().write(mapper.writeValueAsString(responseData));
        } catch (Exception e) {
            Map<String, String> responseData = new HashMap<>();
            responseData.put("status", "error");
            responseData.put("message", e.getMessage());
            response.getWriter().write(mapper.writeValueAsString(responseData));
        }
    }

    private void registerLoan(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            loanService.registerLoan(request.getParameter("idUser"), request.getParameter("idBook"));
            Map<String, String> responseData = new HashMap<>();
            responseData.put("status", "success");
            responseData.put("message", "Préstamo registrado exitosamente.");
            response.getWriter().write(mapper.writeValueAsString(responseData));
        } catch (Exception e) {
            Map<String, String> responseData = new HashMap<>();
            responseData.put("status", "error");
            responseData.put("message", e.getMessage());
            response.getWriter().write(mapper.writeValueAsString(responseData));
        }
    }
}
