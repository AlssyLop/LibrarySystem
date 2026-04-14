package dao;

import java.util.List;
import model.userModel;

/**
 * Interface that defines the operations for User entity
 * 
 * @author Usuario
 */
public interface IUserDAO {
    boolean registerUser(userModel user);

    List<userModel> listUsers();

    boolean deleteUser(int idUser);
    boolean updateUser(userModel user);
    userModel searchUser(int idUser);
    List<userModel> listUsersPaginated(int limit, int offset, String query);
    int countUsers(String query);
}
