package dao;

import java.util.List;
import java.util.Map;
import model.AuthorModel;

/**
 * Interface that defines the operations for Author entity
 * @author Usuario
 */
public interface IAuthorDAO {
    boolean registerAuthor(AuthorModel author);
    boolean updateAuthor(AuthorModel author);
    boolean updateAuthorPartial(int idAuthor, Map<String, Object> changes);
    AuthorModel searchAuthor(int idAuthor);
    List<AuthorModel> listAuthorsPaginated(int limit, int offset, String query);
    int countAuthors(String query);
    boolean checkIdAuthorExits(int idAuthor);
}
