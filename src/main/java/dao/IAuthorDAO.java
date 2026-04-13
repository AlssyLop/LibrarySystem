package dao;

import java.util.List;
import model.authorModel;

/**
 * Interface that defines the operations for Author entity
 * @author Usuario
 */
public interface IAuthorDAO {
    boolean registerAuthor(authorModel author);
    List<authorModel> listAuthors();
}
