package dao;

import java.util.List;
import java.util.Map;
import model.authorModel;

/**
 * Interface that defines the operations for Author entity
 * @author Usuario
 */
public interface IAuthorDAO {
    boolean registerAuthor(authorModel author);
    List<authorModel> listAuthors();
    boolean updateAuthor(authorModel author);
    boolean updateAuthorPartial(int idAuthor, Map<String, Object> changes);
    authorModel searchAuthor(int idAuthor);
    List<authorModel> listAuthorsPaginated(int limit, int offset, String query);
    int countAuthors(String query);
    boolean checkIdAuthorExits(int idAuthor);
}
