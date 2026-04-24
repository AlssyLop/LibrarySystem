package dao;

import java.util.List;
import java.util.Map;
import model.bookModel;

/**
 * Interface that defines the operations for Book entity
 * 
 * @author Usuario
 */
public interface IBookDAO {
    boolean registerBook(bookModel book);
    bookModel searchBook(int idBook);
    List<bookModel> listBooks();
    boolean updateBook(bookModel book);
    boolean updateBookPartial(int idBook, Map<String, Object> changes);
    List<bookModel> listBooksPaginated(int limit, int offset, String query);
    int countBooks(String query);
    boolean checkIsbnExists(String isbn);
    boolean checkIdLibroExits(int idBook);
}
