package dao.impl;

import dao.IUserDAO;
import database.ConnectionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.userModel;

/**
 * Implementation of IUserDAO
 * @author Usuario
 */
public class UserDAO implements IUserDAO {

    // Centralized mapping from ResultSet to userModel object
    private userModel mapResultSetToUser(ResultSet rs) throws SQLException {
        return new userModel(
            rs.getInt("id_user"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("phone")
        );
    }

    @Override
    public boolean registerUser(userModel user) {
        String sql = "INSERT INTO users (name, email, phone) VALUES (?, ?, ?)";
        boolean isRegistered = false;

        try {
            Connection conn = ConnectionDB.connect();
            if (conn != null) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, user.getName());
                    ps.setString(2, user.getEmail());
                    ps.setString(3, user.getPhone());

                    int rowsAffected = ps.executeUpdate();
                    isRegistered = rowsAffected > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error registering user: " + e.getMessage());
        }

        return isRegistered;
    }

    @Override
    public List<userModel> listUsers() {
        String sql = "SELECT id_user, name, email, phone FROM users";
        List<userModel> usersList = new ArrayList<>();

        try {
            Connection conn = ConnectionDB.connect();
            if (conn != null) {
                try (PreparedStatement ps = conn.prepareStatement(sql);
                     ResultSet rs = ps.executeQuery()) {
                    
                    while (rs.next()) {
                        userModel user = mapResultSetToUser(rs);
                        usersList.add(user);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error listing users: " + e.getMessage());
        }

        return usersList;
    }

    @Override
    public boolean deleteUser(int idUser) {
        String sql = "DELETE FROM users WHERE id_user = ?";
        boolean isDeleted = false;

        try {
            Connection conn = ConnectionDB.connect();
            if (conn != null) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, idUser);

                    int rowsAffected = ps.executeUpdate();
                    isDeleted = rowsAffected > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error deleting user: " + e.getMessage());
        }

        return isDeleted;
    }
}
