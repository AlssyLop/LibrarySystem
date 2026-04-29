package dao.impl;

import dao.IBookDAO;
import database.ConnectionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import model.BookModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author Usuario
 */
public class BookDAO implements IBookDAO {
    private static final Logger logger = LoggerFactory.getLogger(BookDAO.class);


    private BookModel mapResultSetToBook(ResultSet rs) throws SQLException {
        BookModel b = new BookModel(
                rs.getInt("id_book"),
                rs.getString("title"),
                rs.getString("isbn"),
                rs.getInt("year"),
                rs.getInt("id_author"));

        try {
            b.setAuthorName(rs.getString(6));
        } catch (SQLException ignore) {
        }

        return b;
    }

    @Override
    public boolean registerBook(BookModel book) {
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
            logger.error("Error registering book: ", e);
        }

        return isRegistered;
    }

    @Override
    public BookModel searchBook(int idBook) {
        String sql = "SELECT b.id_book, b.title, b.isbn, b.year, b.id_author, a.name AS author_name " +
                "FROM books b LEFT JOIN authors a ON b.id_author = a.id_author WHERE b.id_book = ?";
        BookModel book = null;

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
            logger.error("Error searching book: ", e);
        }

        return book;
    }

    @Override
    public List<BookModel> listBooks() {
        String sql = "SELECT b.id_book, b.title, b.isbn, b.year, b.id_author, a.name AS author_name " +
                "FROM books b LEFT JOIN authors a ON b.id_author = a.id_author";
        List<BookModel> booksList = new ArrayList<>();

        try {
            Connection conn = ConnectionDB.connect();
            if (conn != null) {
                try (PreparedStatement ps = conn.prepareStatement(sql);
                        ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {
                        BookModel book = mapResultSetToBook(rs);
                        booksList.add(book);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error listing books: ", e);
        }

        return booksList;
    }

    @Override
    public boolean updateBookPartial(int idBook, Map<String, Object> changes) {
        if (changes == null || changes.isEmpty())
            return false;

        StringBuilder sql = new StringBuilder("UPDATE books SET ");
        List<Object> params = new ArrayList<>();

        for (Map.Entry<String, Object> entry : changes.entrySet()) {
            sql.append(entry.getKey()).append(" = ?, ");
            params.add(entry.getValue());
        }
        sql.setLength(sql.length() - 2);
        sql.append(" WHERE id_book = ?");
        params.add(idBook);

        boolean isUpdated = false;
        try {
            Connection conn = ConnectionDB.connect();
            if (conn != null) {
                try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
                    for (int i = 0; i < params.size(); i++) {
                        Object val = params.get(i);
                        if (val instanceof String) {
                            ps.setString(i + 1, (String) val);
                        } else if (val instanceof Integer) {
                            ps.setInt(i + 1, (Integer) val);
                        }
                    }
                    isUpdated = ps.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) {
            logger.error("Error updating book partial: ", e);
        }
        return isUpdated;
    }

    @Override
    public List<BookModel> listBooksPaginated(int limit, int offset, String query) {
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

        List<BookModel> booksList = new ArrayList<>();

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
                            BookModel book = mapResultSetToBook(rs);
                            booksList.add(book);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error listing books paginated: ", e);
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
            logger.error("Error counting books: ", e);
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
            logger.error("Error checking ISBN: ", e);
        }

        return exists;
    }

    @Override
    public boolean checkIdLibroExits(int idBook) {
        String sql = "SELECT 1 FROM books WHERE id_book = ? LIMIT 1";
        boolean exists = false;

        try {
            Connection conn = ConnectionDB.connect();
            if (conn != null) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, idBook);
                    try (ResultSet rs = ps.executeQuery()) {
                        exists = rs.next();
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error checking ID book: ", e);
        }

        return exists;
    }
}
