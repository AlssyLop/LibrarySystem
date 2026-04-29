package dao;

import java.util.List;
import java.util.Map;
import model.UserModel;

/**
 * Interface that defines the operations for User entity
 * 
 * @author Usuario
 */
public interface IUserDAO {
    boolean registerUser(UserModel user);

    List<UserModel> listUsers();

    boolean deleteUser(int idUser);
    boolean updateUser(UserModel user);
    boolean updateUserPartial(int idUser, Map<String, Object> changes);
    UserModel searchUser(int idUser);
    List<UserModel> listUsersPaginated(int limit, int offset, String query);
    int countUsers(String query);
    boolean checkEmailExists(String email);
    boolean checkIdUserExists(int idUser);
}
