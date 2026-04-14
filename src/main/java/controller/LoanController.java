package controller;

import dao.ILoanDAO;
import dao.impl.LoanDAO;
import java.sql.Date;
import java.util.List;
import model.loanModel;

/**
 * Controlador para la entidad Loan
 * 
 * @author Usuario
 */
public class LoanController {

    private ILoanDAO loanDAO;

    public LoanController() {
        this.loanDAO = new LoanDAO();
    }

    public boolean registerLoan(Date loanDate, Date returnDate, int idUser, int idBook) {
        // Al registrar un préstamo comúnmente no se tiene la fecha de devolución
        // (returnDate es null)
        loanModel newLoan = new loanModel(0, loanDate, returnDate, idUser, idBook);
        return loanDAO.registerLoan(newLoan);
    }

    public boolean returnBook(int idLoan, Date returnDate) {
        return loanDAO.returnBook(idLoan, returnDate);
    }

    public List<loanModel> loanHistory() {
        return loanDAO.loanHistory();
    }

    public List<loanModel> listActiveLoans() {
        return loanDAO.listActiveLoans();
    }

    public List<loanModel> listActiveLoansPaginated(int limit, int offset, Integer idUserSearch, Date dateFilter) {
        return loanDAO.listActiveLoansPaginated(limit, offset, idUserSearch, dateFilter);
    }

    public int countActiveLoans(Integer idUserSearch, Date dateFilter) {
        return loanDAO.countActiveLoans(idUserSearch, dateFilter);
    }

    public List<loanModel> loanHistoryPaginated(int limit, int offset) {
        return loanDAO.loanHistoryPaginated(limit, offset);
    }

    public int countHistoryLoans() {
        return loanDAO.countHistoryLoans();
    }
}
