package dao.impl;

import dao.IBookDAO;
import database.ConnectionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.bookModel;

/**
 *
 * @author Usuario
 */
public class BookDAO implements IBookDAO {

    // Centralized mapping from ResultSet to bookModel object
    private bookModel mapResultSetToBook(ResultSet rs) throws SQLException {
        return new bookModel(
            rs.getInt("id_book"),
            rs.getString("title"),
            rs.getString("isbn"),
            rs.getInt("year"),
            rs.getInt("id_author")
        );
    }

    @Override
    public boolean registerBook(bookModel book) {
        String sql = "INSERT INTO books (title, isbn, year, id_author) VALUES (?, ?, ?, ?)";
        boolean isRegistered = false;

        try {
            Connection conn = ConnectionDB.connect();
            if (conn != null) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, book.getTitle());
                    ps.setString(2, book.getIsbn());
                    ps.setInt(3, book.getYear());
                    ps.setInt(4, book.getIdAuthor());

                    int rowsAffected = ps.executeUpdate();
                    isRegistered = rowsAffected > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error registering book: " + e.getMessage());
        }

        return isRegistered;
    }

    @Override
    public bookModel searchBook(int idBook) {
        String sql = "SELECT id_book, title, isbn, year, id_author FROM books WHERE id_book = ?";
        bookModel book = null;

        try {
            Connection conn = ConnectionDB.connect();
            if (conn != null) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, idBook);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            book = mapResultSetToBook(rs);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error searching book: " + e.getMessage());
        }

        return book;
    }

    @Override
    public List<bookModel> listBooks() {
        String sql = "SELECT id_book, title, isbn, year, id_author FROM books";
        List<bookModel> booksList = new ArrayList<>();

        try {
            Connection conn = ConnectionDB.connect();
            if (conn != null) {
                try (PreparedStatement ps = conn.prepareStatement(sql);
                     ResultSet rs = ps.executeQuery()) {
                    
                    while (rs.next()) {
                        bookModel book = mapResultSetToBook(rs);
                        booksList.add(book);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error listing books: " + e.getMessage());
        }

        return booksList;
    }
}
