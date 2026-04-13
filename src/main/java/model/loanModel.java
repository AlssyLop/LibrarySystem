package model;

import java.sql.Date;

/**
 *
 * @author Usuario
 */
public class loanModel {
    private int idLoan;
    private Date loanDate;
    private Date returnDate;
    private int idUser;
    private int idBook;

    public loanModel(int idLoan, Date loanDate, Date returnDate, int idUser, int idBook) {
        this.idLoan = idLoan;
        this.loanDate = loanDate;
        this.returnDate = returnDate;
        this.idUser = idUser;
        this.idBook = idBook;
    }

    public int getIdLoan() {
        return idLoan;
    }

    public void setIdLoan(int idLoan) {
        this.idLoan = idLoan;
    }

    public Date getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(Date loanDate) {
        this.loanDate = loanDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public int getIdBook() {
        return idBook;
    }

    public void setIdBook(int idBook) {
        this.idBook = idBook;
    }
}
