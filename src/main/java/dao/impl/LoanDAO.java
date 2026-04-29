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
import model.LoanModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author Usuario
 */
public class LoanDAO implements ILoanDAO {
    private static final Logger logger = LoggerFactory.getLogger(LoanDAO.class);


    private LoanModel mapResultSetToLoan(ResultSet rs) throws SQLException {
        boolean returned = false;
        try {
            returned = rs.getBoolean("returned");
        } catch (SQLException e) {
            // Ignorar si no viene en todas las consultas
        }

        LoanModel loan = new LoanModel(
                rs.getInt("id_loan"),
                rs.getDate("loan_date"),
                rs.getDate("return_date"),
                rs.getInt("id_user"),
                rs.getInt("id_book"),
                returned);

        try {
            loan.setUserName(rs.getString(7));
        } catch (SQLException ignore) {}

        try {
            loan.setBookTitle(rs.getString(8));
        } catch (SQLException ignore) {}

        return loan;
    }

    @Override
    public boolean registerLoan(LoanModel loan) {
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
            logger.error("Error registering loan: ", e);
        }

        return isRegistered;
    }

    @Override
    public boolean returnBook(int idLoan) {
        String sql = "UPDATE loans SET return_date = ?, returned = 1 WHERE id_loan = ?";
        Date returnDate = new Date(System.currentTimeMillis()); // Fecha actual de devolucion
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
            logger.error("Error returning book: ", e);
        }

        return isReturned;
    }

    @Override
    public List<LoanModel> loanHistory() {
        String sql = "SELECT l.id_loan, l.loan_date, l.return_date, l.id_user, l.id_book, l.returned, " +
                "u.name AS user_name, b.title AS book_title " +
                "FROM loans l LEFT JOIN users u ON l.id_user = u.id_user LEFT JOIN books b ON l.id_book = b.id_book";
        List<LoanModel> loansList = new ArrayList<>();

        try {
            Connection conn = ConnectionDB.connect();
            if (conn != null) {
                try (PreparedStatement ps = conn.prepareStatement(sql);
                        ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {
                        LoanModel loan = mapResultSetToLoan(rs);
                        loansList.add(loan);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching loan history: ", e);
        }

        return loansList;
    }

    @Override
    public List<LoanModel> listActiveLoans() {
        String sql = "SELECT l.id_loan, l.loan_date, l.return_date, l.id_user, l.id_book, l.returned, " +
                "u.name AS user_name, b.title AS book_title " +
                "FROM loans l LEFT JOIN users u ON l.id_user = u.id_user LEFT JOIN books b ON l.id_book = b.id_book " +
                "WHERE l.returned = 0";
        List<LoanModel> loansList = new ArrayList<>();

        try {
            Connection conn = ConnectionDB.connect();
            if (conn != null) {
                try (PreparedStatement ps = conn.prepareStatement(sql);
                        ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {
                        LoanModel loan = mapResultSetToLoan(rs);
                        loansList.add(loan);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching active loans: ", e);
        }

        return loansList;
    }

    @Override
    public List<LoanModel> listActiveLoansPaginated(int limit, int offset, Integer idUserSearch, Date dateFilter) {
        StringBuilder sql = new StringBuilder(
                "SELECT l.id_loan, l.loan_date, l.return_date, l.id_user, l.id_book, l.returned, " +
                        "u.name AS user_name, b.title AS book_title " +
                        "FROM loans l LEFT JOIN users u ON l.id_user = u.id_user LEFT JOIN books b ON l.id_book = b.id_book "
                        +
                        "WHERE l.returned = 0");
        if (idUserSearch != null)
            sql.append(" AND l.id_user = ?");
        if (dateFilter != null)
            sql.append(" AND DATE(l.loan_date) = ?");
        sql.append(" LIMIT ? OFFSET ?");

        List<LoanModel> loansList = new ArrayList<>();

        try {
            Connection conn = ConnectionDB.connect();
            if (conn != null) {
                try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
                    int paramIndex = 1;
                    if (idUserSearch != null)
                        ps.setInt(paramIndex++, idUserSearch);
                    if (dateFilter != null)
                        ps.setDate(paramIndex++, dateFilter);
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
            logger.error("Error fetching active loans paginated: ", e);
        }

        return loansList;
    }

    @Override
    public int countActiveLoans(Integer idUserSearch, Date dateFilter) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM loans l WHERE l.returned = 0");
        if (idUserSearch != null)
            sql.append(" AND l.id_user = ?");
        if (dateFilter != null)
            sql.append(" AND DATE(l.loan_date) = ?");

        int total = 0;
        try {
            Connection conn = ConnectionDB.connect();
            if (conn != null) {
                try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
                    int paramIndex = 1;
                    if (idUserSearch != null)
                        ps.setInt(paramIndex++, idUserSearch);
                    if (dateFilter != null)
                        ps.setDate(paramIndex, dateFilter);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            total = rs.getInt(1);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error counting active loans: ", e);
        }
        return total;
    }

    @Override
    public List<LoanModel> loanHistoryPaginated(int limit, int offset, Integer idUserSearch, Date dateFilter) {
        StringBuilder sql = new StringBuilder(
                "SELECT l.id_loan, l.loan_date, l.return_date, l.id_user, l.id_book, l.returned, " +
                        "u.name AS user_name, b.title AS book_title " +
                        "FROM loans l LEFT JOIN users u ON l.id_user = u.id_user LEFT JOIN books b ON l.id_book = b.id_book "
                        +
                        "WHERE 1=1");
        if (idUserSearch != null)
            sql.append(" AND l.id_user = ?");
        if (dateFilter != null)
            sql.append(" AND DATE(l.loan_date) = ?");
        sql.append(" LIMIT ? OFFSET ?");

        List<LoanModel> loansList = new ArrayList<>();

        try {
            Connection conn = ConnectionDB.connect();
            if (conn != null) {
                try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
                    int paramIndex = 1;
                    if (idUserSearch != null)
                        ps.setInt(paramIndex++, idUserSearch);
                    if (dateFilter != null)
                        ps.setDate(paramIndex++, dateFilter);
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
            logger.error("Error fetching loan history paginated: ", e);
        }

        return loansList;
    }

    @Override
    public int countHistoryLoans(Integer idUserSearch, Date dateFilter) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM loans l WHERE 1=1");
        if (idUserSearch != null)
            sql.append(" AND l.id_user = ?");
        if (dateFilter != null)
            sql.append(" AND DATE(l.loan_date) = ?");

        int total = 0;
        try {
            Connection conn = ConnectionDB.connect();
            if (conn != null) {
                try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
                    int paramIndex = 1;
                    if (idUserSearch != null)
                        ps.setInt(paramIndex++, idUserSearch);
                    if (dateFilter != null)
                        ps.setDate(paramIndex, dateFilter);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            total = rs.getInt(1);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error counting loan history: ", e);
        }
        return total;
    }

    @Override
    public boolean checkActiveLoanExists(int idUser, int idBook) {
        String sql = "SELECT 1 FROM loans WHERE id_user = ? AND id_book = ? AND returned = 0 LIMIT 1";
        boolean exists = false;

        try {
            Connection conn = ConnectionDB.connect();
            if (conn != null) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, idUser);
                    ps.setInt(2, idBook);
                    try (ResultSet rs = ps.executeQuery()) {
                        exists = rs.next();
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error checking active loan: ", e);
        }

        return exists;
    }
}
