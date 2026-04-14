package controller;

import dao.IAuthorDAO;
import dao.impl.AuthorDAO;
import java.util.List;
import model.authorModel;

/**
 * Controlador para la entidad Author
 * @author Usuario
 */
public class AuthorController {
    
    private IAuthorDAO authorDAO;

    public AuthorController() {
        this.authorDAO = new AuthorDAO();
    }

    public boolean registerAuthor(String name, String nationality) {
        authorModel newAuthor = new authorModel(0, name, nationality);
        return authorDAO.registerAuthor(newAuthor);
    }

    public List<authorModel> listAuthors() {
        return authorDAO.listAuthors();
    }

    public boolean updateAuthor(int idAuthor, String name, String nationality) {
        authorModel author = new authorModel(idAuthor, name, nationality);
        return authorDAO.updateAuthor(author);
    }

    public authorModel searchAuthor(int idAuthor) {
        return authorDAO.searchAuthor(idAuthor);
    }

    public List<authorModel> listAuthorsPaginated(int limit, int offset, String query) {
        return authorDAO.listAuthorsPaginated(limit, offset, query);
    }

    public int countAuthors(String query) {
        return authorDAO.countAuthors(query);
    }
}
