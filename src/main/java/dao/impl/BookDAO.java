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

    private bookModel mapResultSetToBook(ResultSet rs) throws SQLException {
        bookModel b = new bookModel(
                rs.getInt("id_book"),
                rs.getString("title"),
                rs.getString("isbn"),
                rs.getInt("year"),
                rs.getInt("id_author"));
                
        try {
            b.setAuthorName(rs.getString(6));
        } catch (SQLException ignore) {}
        
        return b;
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
        String sql = "SELECT b.id_book, b.title, b.isbn, b.year, b.id_author, a.name AS author_name " +
                "FROM books b LEFT JOIN authors a ON b.id_author = a.id_author WHERE b.id_book = ?";
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
        String sql = "SELECT b.id_book, b.title, b.isbn, b.year, b.id_author, a.name AS author_name " +
                "FROM books b LEFT JOIN authors a ON b.id_author = a.id_author";
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

    @Override
    public boolean updateBook(bookModel book) {
        String sql = "UPDATE books SET title = ?, isbn = ?, year = ?, id_author = ? WHERE id_book = ?";
        boolean isUpdated = false;

        try {
            Connection conn = ConnectionDB.connect();
            if (conn != null) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, book.getTitle());
                    ps.setString(2, book.getIsbn());
                    ps.setInt(3, book.getYear());
                    ps.setInt(4, book.getIdAuthor());
                    ps.setInt(5, book.getIdBook());

                    int rowsAffected = ps.executeUpdate();
                    isUpdated = rowsAffected > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error updating book: " + e.getMessage());
        }

        return isUpdated;
    }

    @Override
    public List<bookModel> listBooksPaginated(int limit, int offset, String query) {
        String sql;
        boolean hasQuery = query != null && !query.trim().isEmpty();

        if (hasQuery) {
            sql = "SELECT b.id_book, b.title, b.isbn, b.year, b.id_author, a.name AS author_name " +
                    "FROM books b LEFT JOIN authors a ON b.id_author = a.id_author " +
                    "WHERE b.title LIKE ? OR b.isbn LIKE ? LIMIT ? OFFSET ?";
        } else {
            sql = "SELECT b.id_book, b.title, b.isbn, b.year, b.id_author, a.name AS author_name " +
                    "FROM books b LEFT JOIN authors a ON b.id_author = a.id_author LIMIT ? OFFSET ?";
        }

        List<bookModel> booksList = new ArrayList<>();

        try {
            Connection conn = ConnectionDB.connect();
            if (conn != null) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {

                    if (hasQuery) {
                        String likeQuery = "%" + query.trim() + "%";
                        ps.setString(1, likeQuery);
                        ps.setString(2, likeQuery);
                        ps.setInt(3, limit);
                        ps.setInt(4, offset);
                    } else {
                        ps.setInt(1, limit);
                        ps.setInt(2, offset);
                    }

                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            bookModel book = mapResultSetToBook(rs);
                            booksList.add(book);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error listing books paginated: " + e.getMessage());
        }

        return booksList;
    }

    @Override
    public int countBooks(String query) {
        String sql;
        boolean hasQuery = query != null && !query.trim().isEmpty();

        if (hasQuery) {
            sql = "SELECT COUNT(*) FROM books WHERE title LIKE ? OR isbn LIKE ?";
        } else {
            sql = "SELECT COUNT(*) FROM books";
        }

        int count = 0;

        try {
            Connection conn = ConnectionDB.connect();
            if (conn != null) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {

                    if (hasQuery) {
                        String likeQuery = "%" + query.trim() + "%";
                        ps.setString(1, likeQuery);
                        ps.setString(2, likeQuery);
                    }

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            count = rs.getInt(1);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error counting books: " + e.getMessage());
        }

        return count;
    }

    @Override
    public boolean checkIsbnExists(String isbn) {
        String sql = "SELECT 1 FROM books WHERE isbn = ? LIMIT 1";
        boolean exists = false;

        try {
            Connection conn = ConnectionDB.connect();
            if (conn != null) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, isbn);
                    try (ResultSet rs = ps.executeQuery()) {
                        exists = rs.next();
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error checking ISBN: " + e.getMessage());
        }

        return exists;
    }
}
