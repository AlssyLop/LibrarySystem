package dao;

import java.sql.Date;
import java.util.List;
import model.LoanModel;

/**
 * Interface that defines the operations for Loan entity
 * @author Usuario
 */
public interface ILoanDAO {
    boolean registerLoan(LoanModel loan);
    boolean returnBook(int idLoan);
    List<LoanModel> loanHistory();
    List<LoanModel> listActiveLoans();
    List<LoanModel> listActiveLoansPaginated(int limit, int offset, Integer idUserSearch, Date dateFilter);
    int countActiveLoans(Integer idUserSearch, Date dateFilter);
    List<LoanModel> loanHistoryPaginated(int limit, int offset, Integer idUserSearch, Date dateFilter);
    int countHistoryLoans(Integer idUserSearch, Date dateFilter);
    boolean checkActiveLoanExists(int idUser, int idBook);
}
