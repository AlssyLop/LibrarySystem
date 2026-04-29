package dao;

import java.util.List;
import java.util.Map;
import model.BookModel;

/**
 * Interface that defines the operations for Book entity
 * 
 * @author Usuario
 */
public interface IBookDAO {
    boolean registerBook(BookModel book);
    BookModel searchBook(int idBook);
    List<BookModel> listBooks();
    boolean updateBookPartial(int idBook, Map<String, Object> changes);
    List<BookModel> listBooksPaginated(int limit, int offset, String query);
    int countBooks(String query);
    boolean checkIsbnExists(String isbn);
    boolean checkIdLibroExits(int idBook);
}
