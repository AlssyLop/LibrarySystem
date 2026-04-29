package service;

import dao.ILoanDAO;
import dao.IUserDAO;
import dao.IBookDAO;
import dao.impl.LoanDAO;
import dao.impl.UserDAO;
import dao.impl.BookDAO;
import model.LoanModel;
import java.sql.Date;
import java.util.List;

public class LoanService {

    private ILoanDAO loanDAO = new LoanDAO();
    private IUserDAO userDAO = new UserDAO();
    private IBookDAO bookDAO = new BookDAO();

    public List<LoanModel> listActiveLoansPaginated(int limit, int offset, Integer idUserSearch, Date dateFilter) {
        return loanDAO.listActiveLoansPaginated(limit, offset, idUserSearch, dateFilter);
    }

    public List<LoanModel> loanHistoryPaginated(int limit, int offset, Integer idUserSearchHist, Date dateFilterHist) {
        return loanDAO.loanHistoryPaginated(limit, offset, idUserSearchHist, dateFilterHist);
    }

    public int countActiveLoans(Integer idUserSearch, Date dateFilter) {
        return loanDAO.countActiveLoans(idUserSearch, dateFilter);
    }

    public int countHistoryLoans(Integer idUserSearchHist, Date dateFilterHist) {
        return loanDAO.countHistoryLoans(idUserSearchHist, dateFilterHist);
    }

    public void registerLoan(String idUserStr, String idBookStr) throws Exception {
        LoanModel loan = validateLoan(idUserStr, idBookStr);
        boolean ok = loanDAO.registerLoan(loan);
        if (!ok) {
            throw new Exception("Error al registrar el préstamo.");
        }
    }
    
    public void returnBook(int idLoan) throws Exception {
        boolean ok = loanDAO.returnBook(idLoan);
        if (!ok) {
            throw new Exception("Error al registrar el préstamo.");
        }
    }

    private LoanModel validateLoan(String idUserStr, String idBookStr) throws Exception {
        String message = "";
        try {
            message = "Debe seleccionar un usuario.";
            messageException(idUserStr == null || idUserStr.trim().isEmpty(), message);
            idUserStr = idUserStr.replaceAll("\\s+", "");
            message = "Usuario inválido";
            int idUser = Integer.parseInt(idUserStr);
            messageException(idUser < 1, message);
            messageException(!this.userDAO.checkIdUserExists(idUser), "Usuario no válido. No se encuentra registrado.");

            message = "Debe seleccionar un libro.";
            messageException(idBookStr == null || idBookStr.trim().isEmpty(), message);
            idBookStr = idBookStr.replaceAll("\\s+", "");
            message = "Libro inválido";
            int idBook = Integer.parseInt(idBookStr);
            messageException(idBook < 1, message);
            messageException(!this.bookDAO.checkIdLibroExits(idBook), "Libro no válido. No se encuentra registrado.");

            messageException(this.loanDAO.checkActiveLoanExists(idUser, idBook), "El usuario ya tiene un préstamo activo de este libro.");

            Date loanDate = new Date(System.currentTimeMillis());
            return new LoanModel(0, loanDate, null, idUser, idBook);

        } catch (NullPointerException | NumberFormatException e) {
            throw new Exception(message);
        }
    }

    private void messageException(boolean condition, String message) throws Exception {
        if (condition) throw new Exception(message);
    }
}
