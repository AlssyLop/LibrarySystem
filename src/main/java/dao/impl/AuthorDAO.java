package dao.impl;

import dao.IAuthorDAO;
import database.ConnectionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.authorModel;

/**
 *
 * @author Usuario
 */
public class AuthorDAO implements IAuthorDAO {

    // Centralized mapping from ResultSet to authorModel object
    private authorModel mapResultSetToAuthor(ResultSet rs) throws SQLException {
        return new authorModel(
                rs.getInt("id_author"),
                rs.getString("name"),
                rs.getString("nationality"));
    }

    @Override
    public boolean registerAuthor(authorModel author) {
        System.out.println(author);
        String sql = "INSERT INTO authors (name, nationality) VALUES (?, ?)";
        boolean isRegistered = false;

        try {
            Connection conn = ConnectionDB.connect();
            if (conn != null) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, author.getName());
                    ps.setString(2, author.getNationality());

                    int rowsAffected = ps.executeUpdate();
                    isRegistered = rowsAffected > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error registering author: " + e.getMessage());
        }

        return isRegistered;
    }

    @Override
    public List<authorModel> listAuthors() {
        String sql = "SELECT id_author, name, nationality FROM authors";
        List<authorModel> authorsList = new ArrayList<>();

        try {
            Connection conn = ConnectionDB.connect();
            if (conn != null) {
                try (PreparedStatement ps = conn.prepareStatement(sql);
                        ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {
                        authorModel author = mapResultSetToAuthor(rs);
                        authorsList.add(author);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error listing authors: " + e.getMessage());
        }

        return authorsList;
    }

    @Override
    public boolean updateAuthor(authorModel author) {
        String sql = "UPDATE authors SET name = ?, nationality = ? WHERE id_author = ?";
        boolean isUpdated = false;

        try {
            Connection conn = ConnectionDB.connect();
            if (conn != null) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, author.getName());
                    ps.setString(2, author.getNationality());
                    ps.setInt(3, author.getIdAuthor());

                    int rowsAffected = ps.executeUpdate();
                    isUpdated = rowsAffected > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error updating author: " + e.getMessage());
        }

        return isUpdated;
    }

    @Override
    public authorModel searchAuthor(int idAuthor) {
        String sql = "SELECT id_author, name, nationality FROM authors WHERE id_author = ?";
        authorModel author = null;

        try {
            Connection conn = ConnectionDB.connect();
            if (conn != null) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, idAuthor);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            author = mapResultSetToAuthor(rs);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error searching author: " + e.getMessage());
        }

        return author;
    }

    @Override
    public List<authorModel> listAuthorsPaginated(int limit, int offset, String query) {
        String sql;
        boolean hasQuery = query != null && !query.trim().isEmpty();

        if (hasQuery) {
            sql = "SELECT id_author, name, nationality FROM authors WHERE name LIKE ? LIMIT ? OFFSET ?";
        } else {
            sql = "SELECT id_author, name, nationality FROM authors LIMIT ? OFFSET ?";
        }
        
        List<authorModel> authorsList = new ArrayList<>();

        try {
            Connection conn = ConnectionDB.connect();
            if (conn != null) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    if (hasQuery) {
                        String likeQuery = "%" + query.trim() + "%";
                        ps.setString(1, likeQuery);
                        ps.setInt(2, limit);
                        ps.setInt(3, offset);
                    } else {
                        ps.setInt(1, limit);
                        ps.setInt(2, offset);
                    }

                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            authorModel author = mapResultSetToAuthor(rs);
                            authorsList.add(author);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error listing authors paginated: " + e.getMessage());
        }

        return authorsList;
    }

    @Override
    public int countAuthors(String query) {
        String sql;
        boolean hasQuery = query != null && !query.trim().isEmpty();

        if (hasQuery) {
            sql = "SELECT COUNT(*) FROM authors WHERE name LIKE ?";
        } else {
            sql = "SELECT COUNT(*) FROM authors";
        }
        
        int count = 0;

        try {
            Connection conn = ConnectionDB.connect();
            if (conn != null) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    if (hasQuery) {
                        String likeQuery = "%" + query.trim() + "%";
                        ps.setString(1, likeQuery);
                    }

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            count = rs.getInt(1);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error counting authors: " + e.getMessage());
        }

        return count;
    }
}
