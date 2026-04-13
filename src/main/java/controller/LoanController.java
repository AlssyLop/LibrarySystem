package controller;

import dao.ILoanDAO;
import dao.impl.LoanDAO;
import java.sql.Date;
import java.util.List;
import model.loanModel;

/**
 * Controlador para la entidad Loan
 * @author Usuario
 */
public class LoanController {
    
    private ILoanDAO loanDAO;

    public LoanController() {
        this.loanDAO = new LoanDAO();
    }

    public boolean registerLoan(Date loanDate, int idUser, int idBook) {
        // Al registrar un préstamo comúnmente no se tiene la fecha de devolución (returnDate es null)
        loanModel newLoan = new loanModel(0, loanDate, null, idUser, idBook);
        return loanDAO.registerLoan(newLoan);
    }

    public boolean returnBook(int idLoan, Date returnDate) {
        return loanDAO.returnBook(idLoan, returnDate);
    }

    public List<loanModel> loanHistory() {
        return loanDAO.loanHistory();
    }
}
