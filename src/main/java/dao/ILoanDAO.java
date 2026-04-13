package dao;

import java.sql.Date;
import java.util.List;
import model.loanModel;

/**
 * Interface that defines the operations for Loan entity
 * @author Usuario
 */
public interface ILoanDAO {
    boolean registerLoan(loanModel loan);
    boolean returnBook(int idLoan, Date returnDate);
    List<loanModel> loanHistory();
}
