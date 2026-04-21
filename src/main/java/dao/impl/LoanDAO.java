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
        boolean returned = false;
        try {
            returned = rs.getBoolean("returned");
        } catch (SQLException e) {
            // Ignorar si no viene en todas las consultas
        }
        
        loanModel loan = new loanModel(
            rs.getInt("id_loan"),
            rs.getDate("loan_date"),
            rs.getDate("return_date"),
            rs.getInt("id_user"),
            rs.getInt("id_book"),
            returned
        );
        
        try {
            loan.setUserName(rs.getString("user_name"));
        } catch (SQLException ignore) {}
        
        try {
            loan.setBookTitle(rs.getString("book_title"));
        } catch (SQLException ignore) {}

        return loan;
    }

    @Override
    public boolean registerLoan(loanModel loan) {
        String sql = "INSERT INTO loans (loan_date, return_date, id_user, id_book, returned) VALUES (?, ?, ?, ?, ?)";
        boolean isRegistered = false;

        try {
            Connection conn = ConnectionDB.connect();
            if (conn != null) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setDate(1, loan.getLoanDate());
                    ps.setDate(2, loan.getReturnDate()); // Puede ser nulo inicialmente
                    ps.setInt(3, loan.getIdUser());
                    ps.setInt(4, loan.getIdBook());
                    ps.setBoolean(5, loan.isReturned());

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
        String sql = "UPDATE loans SET return_date = ?, returned = 1 WHERE id_loan = ?";
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
        String sql = "SELECT l.id_loan, l.loan_date, l.return_date, l.id_user, l.id_book, l.returned, " +
                     "u.name AS user_name, b.title AS book_title " +
                     "FROM loans l LEFT JOIN users u ON l.id_user = u.id_user LEFT JOIN books b ON l.id_book = b.id_book";
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

    @Override
    public List<loanModel> listActiveLoans() {
        String sql = "SELECT l.id_loan, l.loan_date, l.return_date, l.id_user, l.id_book, l.returned, " +
                     "u.name AS user_name, b.title AS book_title " +
                     "FROM loans l LEFT JOIN users u ON l.id_user = u.id_user LEFT JOIN books b ON l.id_book = b.id_book " +
                     "WHERE l.returned = 0";
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
            System.out.println("Error fetching active loans: " + e.getMessage());
        }

        return loansList;
    }

    @Override
    public List<loanModel> listActiveLoansPaginated(int limit, int offset, Integer idUserSearch, Date dateFilter) {
        StringBuilder sql = new StringBuilder("SELECT l.id_loan, l.loan_date, l.return_date, l.id_user, l.id_book, l.returned, " +
                                              "u.name AS user_name, b.title AS book_title " +
                                              "FROM loans l LEFT JOIN users u ON l.id_user = u.id_user LEFT JOIN books b ON l.id_book = b.id_book " +
                                              "WHERE l.returned = 0");
        if (idUserSearch != null) sql.append(" AND l.id_user = ?");
        if (dateFilter != null) sql.append(" AND DATE(l.loan_date) = ?");
        sql.append(" LIMIT ? OFFSET ?");
        
        List<loanModel> loansList = new ArrayList<>();

        try {
            Connection conn = ConnectionDB.connect();
            if (conn != null) {
                try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
                    int paramIndex = 1;
                    if (idUserSearch != null) ps.setInt(paramIndex++, idUserSearch);
                    if (dateFilter != null) ps.setDate(paramIndex++, dateFilter);
                    ps.setInt(paramIndex++, limit);
                    ps.setInt(paramIndex, offset);
                    
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            loansList.add(mapResultSetToLoan(rs));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching active loans paginated: " + e.getMessage());
        }

        return loansList;
    }

    @Override
    public int countActiveLoans(Integer idUserSearch, Date dateFilter) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM loans l WHERE l.returned = 0");
        if (idUserSearch != null) sql.append(" AND l.id_user = ?");
        if (dateFilter != null) sql.append(" AND DATE(l.loan_date) = ?");
        
        int total = 0;
        try {
            Connection conn = ConnectionDB.connect();
            if (conn != null) {
                try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
                    int paramIndex = 1;
                    if (idUserSearch != null) ps.setInt(paramIndex++, idUserSearch);
                    if (dateFilter != null) ps.setDate(paramIndex, dateFilter);
                    
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            total = rs.getInt(1);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error counting active loans: " + e.getMessage());
        }
        return total;
    }

    @Override
    public List<loanModel> loanHistoryPaginated(int limit, int offset, Integer idUserSearch, Date dateFilter) {
        StringBuilder sql = new StringBuilder("SELECT l.id_loan, l.loan_date, l.return_date, l.id_user, l.id_book, l.returned, " +
                                              "u.name AS user_name, b.title AS book_title " +
                                              "FROM loans l LEFT JOIN users u ON l.id_user = u.id_user LEFT JOIN books b ON l.id_book = b.id_book " +
                                              "WHERE 1=1");
        if (idUserSearch != null) sql.append(" AND l.id_user = ?");
        if (dateFilter != null) sql.append(" AND DATE(l.loan_date) = ?");
        sql.append(" LIMIT ? OFFSET ?");
        
        List<loanModel> loansList = new ArrayList<>();

        try {
            Connection conn = ConnectionDB.connect();
            if (conn != null) {
                try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
                    int paramIndex = 1;
                    if (idUserSearch != null) ps.setInt(paramIndex++, idUserSearch);
                    if (dateFilter != null) ps.setDate(paramIndex++, dateFilter);
                    ps.setInt(paramIndex++, limit);
                    ps.setInt(paramIndex, offset);
                    
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            loansList.add(mapResultSetToLoan(rs));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching loan history paginated: " + e.getMessage());
        }

        return loansList;
    }

    @Override
    public int countHistoryLoans(Integer idUserSearch, Date dateFilter) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM loans l WHERE 1=1");
        if (idUserSearch != null) sql.append(" AND l.id_user = ?");
        if (dateFilter != null) sql.append(" AND DATE(l.loan_date) = ?");
        
        int total = 0;
        try {
            Connection conn = ConnectionDB.connect();
            if (conn != null) {
                try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
                    int paramIndex = 1;
                    if (idUserSearch != null) ps.setInt(paramIndex++, idUserSearch);
                    if (dateFilter != null) ps.setDate(paramIndex, dateFilter);
                    
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            total = rs.getInt(1);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error counting loan history: " + e.getMessage());
        }
        return total;
    }
}
