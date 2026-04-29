package dao.impl;

import dao.IUserDAO;
import database.ConnectionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import model.UserModel;

/**
 * Implementation of IUserDAO
 * @author Usuario
 */
public class UserDAO implements IUserDAO {

    // Centralized mapping from ResultSet to UserModel object
    private UserModel mapResultSetToUser(ResultSet rs) throws SQLException {
        boolean activo = true;
        try {
            activo = rs.getBoolean("activo");
        } catch (SQLException e) {
            // If the column doesn't exist in a specific query, keep it true
        }

        return new UserModel(
            rs.getInt("id_user"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("phone"),
            activo
        );
    }

    @Override
    public boolean registerUser(UserModel user) {
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
    public List<UserModel> listUsers() {
        String sql = "SELECT id_user, name, email, phone, activo FROM users WHERE activo = 1";
        List<UserModel> usersList = new ArrayList<>();

        try {
            Connection conn = ConnectionDB.connect();
            if (conn != null) {
                try (PreparedStatement ps = conn.prepareStatement(sql);
                     ResultSet rs = ps.executeQuery()) {
                    
                    while (rs.next()) {
                        UserModel user = mapResultSetToUser(rs);
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
        String sql = "UPDATE users SET activo = 0 WHERE id_user = ?";
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

    @Override
    public boolean updateUser(UserModel user) {
        String sql = "UPDATE users SET name = ?, email = ?, phone = ?, activo = ? WHERE id_user = ?";
        boolean isUpdated = false;

        try {
            Connection conn = ConnectionDB.connect();
            if (conn != null) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, user.getName());
                    ps.setString(2, user.getEmail());
                    ps.setString(3, user.getPhone());
                    ps.setBoolean(4, user.isActivo());
                    ps.setInt(5, user.getIdUser());

                    int rowsAffected = ps.executeUpdate();
                    isUpdated = rowsAffected > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error updating user: " + e.getMessage());
        }

        return isUpdated;
    }

    @Override
    public UserModel searchUser(int idUser) {
        String sql = "SELECT id_user, name, email, phone, activo FROM users WHERE id_user = ?";
        UserModel user = null;

        try {
            Connection conn = ConnectionDB.connect();
            if (conn != null) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, idUser);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            user = mapResultSetToUser(rs);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error searching user: " + e.getMessage());
        }

        return user;
    }

    @Override
    public List<UserModel> listUsersPaginated(int limit, int offset, String query) {
        String sql;
        boolean hasQuery = query != null && !query.trim().isEmpty();

        if (hasQuery) {
            sql = "SELECT id_user, name, email, phone, activo FROM users WHERE activo = 1 AND (name LIKE ? OR email LIKE ?) LIMIT ? OFFSET ?";
        } else {
            sql = "SELECT id_user, name, email, phone, activo FROM users WHERE activo = 1 LIMIT ? OFFSET ?";
        }
        
        List<UserModel> usersList = new ArrayList<>();

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
                            UserModel user = mapResultSetToUser(rs);
                            usersList.add(user);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error listing users paginated: " + e.getMessage());
        }

        return usersList;
    }

    @Override
    public int countUsers(String query) {
        String sql;
        boolean hasQuery = query != null && !query.trim().isEmpty();

        if (hasQuery) {
            sql = "SELECT COUNT(*) FROM users WHERE activo = 1 AND (name LIKE ? OR email LIKE ?)";
        } else {
            sql = "SELECT COUNT(*) FROM users WHERE activo = 1";
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
            System.out.println("Error counting users: " + e.getMessage());
        }

        return count;
    }

    @Override
    public boolean updateUserPartial(int idUser, Map<String, Object> changes) {
        if (changes == null || changes.isEmpty())
            return false;

        StringBuilder sql = new StringBuilder("UPDATE users SET ");
        List<Object> params = new ArrayList<>();

        for (Map.Entry<String, Object> entry : changes.entrySet()) {
            sql.append(entry.getKey()).append(" = ?, ");
            params.add(entry.getValue());
        }
        sql.setLength(sql.length() - 2);
        sql.append(" WHERE id_user = ?");
        params.add(idUser);

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
                        } else if (val instanceof Boolean) {
                            ps.setBoolean(i + 1, (Boolean) val);
                        }
                    }
                    isUpdated = ps.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error updating user partial: " + e.getMessage());
        }
        return isUpdated;
    }

    @Override
    public boolean checkEmailExists(String email) {
        String sql = "SELECT 1 FROM users WHERE email = ? AND activo = 1 LIMIT 1";
        boolean exists = false;

        try {
            Connection conn = ConnectionDB.connect();
            if (conn != null) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, email);
                    try (ResultSet rs = ps.executeQuery()) {
                        exists = rs.next();
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error checking email: " + e.getMessage());
        }

        return exists;
    }

    @Override
    public boolean checkIdUserExists(int idUser) {
        String sql = "SELECT 1 FROM users WHERE id_user = ? AND activo = 1 LIMIT 1";
        boolean exists = false;

        try {
            Connection conn = ConnectionDB.connect();
            if (conn != null) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, idUser);
                    try (ResultSet rs = ps.executeQuery()) {
                        exists = rs.next();
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error checking ID user: " + e.getMessage());
        }

        return exists;
    }
}
