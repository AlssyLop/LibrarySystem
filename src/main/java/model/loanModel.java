package model;

import java.sql.Date;

/**
 *
 * @author Usuario
 */
public class LoanModel {
    private int idLoan;
    private Date loanDate;
    private Date returnDate;
    private int idUser;
    private int idBook;
    private boolean returned;
    
    // Campos extra para mostrar detalles en las vistas
    private String userName;
    private String bookTitle;

    public LoanModel(int idLoan, Date loanDate, Date returnDate, int idUser, int idBook, boolean returned) {
        this.idLoan = idLoan;
        this.loanDate = loanDate;
        this.returnDate = returnDate;
        this.idUser = idUser;
        this.idBook = idBook;
        this.returned = returned;
    }

    public LoanModel(int idLoan, Date loanDate, Date returnDate, int idUser, int idBook) {
        this.idLoan = idLoan;
        this.loanDate = loanDate;
        this.returnDate = returnDate;
        this.idUser = idUser;
        this.idBook = idBook;
        this.returned = false; // Default falso
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

    public boolean isReturned() {
        return returned;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }
}
