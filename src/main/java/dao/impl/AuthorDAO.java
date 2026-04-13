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
            rs.getString("nationality")
        );
    }

    @Override
    public boolean registerAuthor(authorModel author) {
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
}
