package dao;

import java.util.List;
import java.util.Map;
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
    boolean updateUserPartial(int idUser, Map<String, Object> changes);
    userModel searchUser(int idUser);
    List<userModel> listUsersPaginated(int limit, int offset, String query);
    int countUsers(String query);
    boolean checkEmailExists(String email);
    boolean checkIdUserExists(int idUser);
}
