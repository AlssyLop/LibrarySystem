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
}
