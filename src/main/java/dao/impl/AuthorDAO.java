package dao.impl;

import dao.IAuthorDAO;
import database.ConnectionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import model.AuthorModel;

/**
 *
 * @author Usuario
 */
public class AuthorDAO implements IAuthorDAO {

    // Centralized mapping from ResultSet to AuthorModel object
    private AuthorModel mapResultSetToAuthor(ResultSet rs) throws SQLException {
        return new AuthorModel(
                rs.getInt("id_author"),
                rs.getString("name"),
                rs.getString("nationality"));
    }

    @Override
    public boolean registerAuthor(AuthorModel author) {
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
    public List<AuthorModel> listAuthors() {
        String sql = "SELECT id_author, name, nationality FROM authors";
        List<AuthorModel> authorsList = new ArrayList<>();

        try {
            Connection conn = ConnectionDB.connect();
            if (conn != null) {
                try (PreparedStatement ps = conn.prepareStatement(sql);
                        ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {
                        AuthorModel author = mapResultSetToAuthor(rs);
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
    public boolean updateAuthor(AuthorModel author) {
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
    public boolean updateAuthorPartial(int idAuthor, Map<String, Object> changes) {
        if (changes == null || changes.isEmpty())
            return false;

        StringBuilder sql = new StringBuilder("UPDATE authors SET ");
        List<Object> params = new ArrayList<>();

        for (Map.Entry<String, Object> entry : changes.entrySet()) {
            sql.append(entry.getKey()).append(" = ?, ");
            params.add(entry.getValue());
        }
        sql.setLength(sql.length() - 2);
        sql.append(" WHERE id_author = ?");
        params.add(idAuthor);

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
            System.out.println("Error updating author partial: " + e.getMessage());
        }
        return isUpdated;
    }

    @Override
    public AuthorModel searchAuthor(int idAuthor) {
        String sql = "SELECT id_author, name, nationality FROM authors WHERE id_author = ?";
        AuthorModel author = null;

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
    public List<AuthorModel> listAuthorsPaginated(int limit, int offset, String query) {
        String sql;
        boolean hasQuery = query != null && !query.trim().isEmpty();

        if (hasQuery) {
            sql = "SELECT id_author, name, nationality FROM authors WHERE name LIKE ? LIMIT ? OFFSET ?";
        } else {
            sql = "SELECT id_author, name, nationality FROM authors LIMIT ? OFFSET ?";
        }
        
        List<AuthorModel> authorsList = new ArrayList<>();

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
                            AuthorModel author = mapResultSetToAuthor(rs);
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
    
    @Override
    public boolean checkIdAuthorExits(int idAuthor){
        String sql = "SELECT 1 FROM authors WHERE id_author = ? LIMIT 1";
        boolean exists = false;

        try {
            Connection conn = ConnectionDB.connect();
            if (conn != null) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, idAuthor);
                    try (ResultSet rs = ps.executeQuery()) {
                        exists = rs.next();
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error checking ID Author: " + e.getMessage());
        }

        return exists;
    }
}
