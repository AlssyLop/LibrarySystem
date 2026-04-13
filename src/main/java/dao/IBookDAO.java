package dao;

import java.util.List;
import model.bookModel;

/**
 * Interface that defines the operations for Book entity
 * @author Usuario
 */
public interface IBookDAO {
    boolean registerBook(bookModel book);
    bookModel searchBook(int idBook);
    List<bookModel> listBooks();
}
