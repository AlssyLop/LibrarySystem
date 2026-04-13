package controller;

import dao.IUserDAO;
import dao.impl.UserDAO;
import java.util.List;
import model.userModel;

/**
 * Controlador para la entidad User
 * Intermediario entre la vista y el acceso a datos (DAO)
 * @author Usuario
 */
public class UserController {
    
    private IUserDAO userDAO;

    public UserController() {
        // Instanciamos la implementación concreta
        this.userDAO = new UserDAO();
    }

    /**
     * Registra un nuevo usuario
     */
    public boolean registerUser(String name, String email, String phone) {
        // Creamos el modelo (el id en 0 o cualquiera, la BD maneja el AUTO_INCREMENT)
        userModel newUser = new userModel(0, name, email, phone);
        return userDAO.registerUser(newUser);
    }

    /**
     * Devuelve la lista de usuarios registrados
     */
    public List<userModel> listUsers() {
        return userDAO.listUsers();
    }

    /**
     * Elimina un usuario por su ID
     */
    public boolean deleteUser(int idUser) {
        return userDAO.deleteUser(idUser);
    }
}
