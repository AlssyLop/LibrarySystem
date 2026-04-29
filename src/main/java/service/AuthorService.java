package service;

import dao.IAuthorDAO;
import dao.impl.AuthorDAO;
import model.AuthorModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AuthorService {

    private IAuthorDAO authorDAO = new AuthorDAO();

    public List<AuthorModel> listAuthorsPaginated(int limit, int offset, String query) {
        return authorDAO.listAuthorsPaginated(limit, offset, query);
    }

    public int countAuthors(String query) {
        return authorDAO.countAuthors(query);
    }

    public AuthorModel searchAuthor(int id) {
        return authorDAO.searchAuthor(id);
    }

    public void registerAuthor(String idAuthor, String name, String nationality) throws Exception {
        AuthorModel author = validateAuthor(true, idAuthor, name, nationality);
        boolean ok = authorDAO.registerAuthor(author);
        if (!ok) {
            throw new Exception("Error al registrar el autor.");
        }
    }

    public void updateAuthor(String idAuthor, String name, String nationality) throws Exception {
        AuthorModel validatedAuthor = validateAuthor(false, idAuthor, name, nationality);
        AuthorModel currentAuthor = authorDAO.searchAuthor(validatedAuthor.getIdAuthor());

        Map<String, Object> changes = new HashMap<>();
        if (!currentAuthor.getName().equals(validatedAuthor.getName())) {
            changes.put("name", validatedAuthor.getName());
        }
        if (!currentAuthor.getNationality().equals(validatedAuthor.getNationality())) {
            changes.put("nationality", validatedAuthor.getNationality());
        }

        if (changes.isEmpty()) {
            throw new Exception("No se detectaron cambios.");
        }

        boolean ok = authorDAO.updateAuthorPartial(validatedAuthor.getIdAuthor(), changes);
        if (!ok) {
            throw new Exception("Error al actualizar el autor.");
        }
    }

    public ArrayList<String> getNationalities() {
        String[] paisesCodigos = Locale.getISOCountries();
        ArrayList<String> nacionalidades = new ArrayList<>();
        for (String codigo : paisesCodigos) {
            Locale locale = new Locale("", codigo);
            nacionalidades.add(locale.getDisplayCountry(new Locale("es", "ES")));
        }
        Collections.sort(nacionalidades);
        return nacionalidades;
    }

    private AuthorModel validateAuthor(boolean isRegister, String idAuthor, String name, String nationality) throws Exception {
        String message = "";
        try {
            int IDautor = 0;
            if (!isRegister) {
                message = "El autor es requerido.";
                messageException(idAuthor == null || idAuthor.trim().isEmpty(), message);
                idAuthor = idAuthor.replaceAll("\\s+", "");
                message = "Autor inválido";
                IDautor = Integer.parseInt(idAuthor);
                messageException(IDautor < 1, message);
                messageException(!this.authorDAO.checkIdAuthorExits(IDautor), "Autor no válido. No se encuentra registrado");
            }

            message = "El nombre es requerido.";
            messageException(name == null || name.trim().isEmpty(), message);
            name = String.join(" ", name.trim().split("\\s+")).toUpperCase();
            messageException(!name.matches("^[\\p{L}][\\p{L} .'-]*$"), "Nombre inválido");
            messageException(name.length() < 3 || name.length() > 100, "El nombre debe tener entre 3 y 100 caracteres.");

            message = "Nacionalidad requerida.";
            messageException(nationality == null || nationality.trim().isEmpty(), message);
            boolean validNationality = getNationalities().stream()
                    .anyMatch(n -> n.equalsIgnoreCase(nationality.trim()));
            messageException(!validNationality, "Nacionalidad inválida.");

            return new AuthorModel(IDautor, name, nationality.trim().toUpperCase());

        } catch (NullPointerException | NumberFormatException e) {
            throw new Exception(message);
        }
    }

    private void messageException(boolean condition, String message) throws Exception {
        if (condition) throw new Exception(message);
    }
}
