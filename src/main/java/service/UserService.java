package service;

import dao.IUserDAO;
import dao.impl.UserDAO;
import model.UserModel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserService {

    private IUserDAO userDAO = new UserDAO();

    public List<UserModel> listUsersPaginated(int limit, int offset, String query) {
        return userDAO.listUsersPaginated(limit, offset, query);
    }

    public int countUsers(String query) {
        return userDAO.countUsers(query);
    }

    public void deleteUser(int id) {
        userDAO.deleteUser(id);
    }

    public void registerUser(String idUser, String name, String email, String phone) throws Exception {
        UserModel user = validateUser(true, idUser, name, email, phone);
        boolean ok = userDAO.registerUser(user);
        if (!ok) {
            throw new Exception("Error al registrar el usuario.");
        }
    }

    public void updateUser(String idUser, String name, String email, String phone) throws Exception {
        UserModel validatedUser = validateUser(false, idUser, name, email, phone);

        UserModel currentUser = userDAO.searchUser(validatedUser.getIdUser());

        Map<String, Object> changes = new HashMap<>();
        if (!currentUser.getName().equals(validatedUser.getName())) {
            changes.put("name", validatedUser.getName());
        }
        if (!currentUser.getEmail().equals(validatedUser.getEmail())) {
            changes.put("email", validatedUser.getEmail());
        }
        if (!currentUser.getPhone().equals(validatedUser.getPhone())) {
            changes.put("phone", validatedUser.getPhone());
        }

        if (changes.isEmpty()) {
            throw new Exception("No se detectaron cambios.");
        }

        boolean ok = userDAO.updateUserPartial(validatedUser.getIdUser(), changes);
        if (!ok) {
            throw new Exception("Error al actualizar el usuario.");
        }
    }

    private UserModel validateUser(boolean isRegister, String idUser, String name, String email, String phone) throws Exception {
        String message = "";
        try {
            int IDuser = 0;
            if (!isRegister) {
                message = "El usuario es requerido.";
                messageException(idUser == null || idUser.trim().isEmpty(), message);
                idUser = idUser.replaceAll("\\s+", "");
                message = "Usuario inválido";
                IDuser = Integer.parseInt(idUser);
                messageException(IDuser < 1, message);
                messageException(!this.userDAO.checkIdUserExists(IDuser), "Usuario no válido. No se encuentra registrado.");
            }

            message = "El nombre es requerido.";
            messageException(name == null || name.trim().isEmpty(), message);
            name = String.join(" ", name.trim().split("\\s+")).toUpperCase();
            messageException(!name.matches("^[\\p{L}][\\p{L} .'-]*$"), "Nombre inválido");
            messageException(name.length() < 3 || name.length() > 100, "El nombre debe tener entre 3 y 100 caracteres.");

            message = "Email requerido.";
            messageException(email == null || email.trim().isEmpty(), message);
            email = email.trim().toLowerCase();
            message = "Email inválido.";
            messageException(email.matches(".*\\s.*"), message);
            String[] emailParts = email.split("@");
            messageException(emailParts.length != 2 || emailParts[0].length() > 50 || emailParts[1].length() > 50, message);
            messageException(!email.matches("^[a-z0-9_\\.\\-]+@(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,}$"), message);
            messageException(email.length() > 100, "Email inválido. Supera el máximo de 100 caracteres.");
            
            if (isRegister) {
                messageException(this.userDAO.checkEmailExists(email), "El email ya se encuentra registrado.");
            } else {
                UserModel currentUser = this.userDAO.searchUser(IDuser);
                if (!currentUser.getEmail().equals(email)) {
                    messageException(this.userDAO.checkEmailExists(email), "El email ya se encuentra asignado a otro usuario.");
                }
            }

            message = "Teléfono requerido.";
            messageException(phone == null || phone.trim().isEmpty(), message);
            phone = phone.replaceAll("\\s+", "");
            boolean hasPlus = phone.startsWith("+");
            phone = phone.replaceAll("[^0-9]", "");
            if (hasPlus) {
                phone = "+" + phone;
            }
            messageException(phone.length() < 10 || phone.length() > 15, "Teléfono inválido. Debe tener entre 10 y 15 caracteres.");

            return new UserModel(IDuser, name, email, phone);

        } catch (NullPointerException | NumberFormatException e) {
            throw new Exception(message);
        }
    }

    private void messageException(boolean condition, String message) throws Exception {
        if (condition) throw new Exception(message);
    }
}
