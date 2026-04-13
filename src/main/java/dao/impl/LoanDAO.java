package dao.impl;

import dao.ILoanDAO;
import database.ConnectionDB;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.loanModel;

/**
 *
 * @author Usuario
 */
public class LoanDAO implements ILoanDAO {

    // Centralized mapping from ResultSet to loanModel object
    private loanModel mapResultSetToLoan(ResultSet rs) throws SQLException {
        return new loanModel(
            rs.getInt("id_loan"),
            rs.getDate("loan_date"),
            rs.getDate("return_date"),
            rs.getInt("id_user"),
            rs.getInt("id_book")
        );
    }

    @Override
    public boolean registerLoan(loanModel loan) {
        String sql = "INSERT INTO loans (loan_date, return_date, id_user, id_book) VALUES (?, ?, ?, ?)";
        boolean isRegistered = false;

        try {
            Connection conn = ConnectionDB.connect();
            if (conn != null) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setDate(1, loan.getLoanDate());
                    ps.setDate(2, loan.getReturnDate()); // Puede ser nulo inicialmente
                    ps.setInt(3, loan.getIdUser());
                    ps.setInt(4, loan.getIdBook());

                    int rowsAffected = ps.executeUpdate();
                    isRegistered = rowsAffected > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error registering loan: " + e.getMessage());
        }

        return isRegistered;
    }

    @Override
    public boolean returnBook(int idLoan, Date returnDate) {
        String sql = "UPDATE loans SET return_date = ? WHERE id_loan = ?";
        boolean isReturned = false;

        try {
            Connection conn = ConnectionDB.connect();
            if (conn != null) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setDate(1, returnDate);
                    ps.setInt(2, idLoan);

                    int rowsAffected = ps.executeUpdate();
                    isReturned = rowsAffected > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error returning book: " + e.getMessage());
        }

        return isReturned;
    }

    @Override
    public List<loanModel> loanHistory() {
        String sql = "SELECT id_loan, loan_date, return_date, id_user, id_book FROM loans";
        List<loanModel> loansList = new ArrayList<>();

        try {
            Connection conn = ConnectionDB.connect();
            if (conn != null) {
                try (PreparedStatement ps = conn.prepareStatement(sql);
                     ResultSet rs = ps.executeQuery()) {
                    
                    while (rs.next()) {
                        loanModel loan = mapResultSetToLoan(rs);
                        loansList.add(loan);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching loan history: " + e.getMessage());
        }

        return loansList;
    }
}
