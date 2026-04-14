package controller;

import dao.IBookDAO;
import dao.impl.BookDAO;
import java.util.List;
import model.bookModel;

/**
 * Controlador para la entidad Book
 * @author Usuario
 */
public class BookController {
    
    private IBookDAO bookDAO;

    public BookController() {
        this.bookDAO = new BookDAO();
    }

    public boolean registerBook(String title, String isbn, int year, int idAuthor) {
        bookModel newBook = new bookModel(0, title, isbn, year, idAuthor);
        return bookDAO.registerBook(newBook);
    }

    public bookModel searchBook(int idBook) {
        return bookDAO.searchBook(idBook);
    }

    public List<bookModel> listBooks() {
        return bookDAO.listBooks();
    }

    public boolean updateBook(int idBook, String title, String isbn, int year, int idAuthor) {
        bookModel book = new bookModel(idBook, title, isbn, year, idAuthor);
        return bookDAO.updateBook(book);
    }

    public List<bookModel> listBooksPaginated(int limit, int offset, String query) {
        return bookDAO.listBooksPaginated(limit, offset, query);
    }

    public int countBooks(String query) {
        return bookDAO.countBooks(query);
    }

    public boolean checkIsbnExists(String isbn) {
        return bookDAO.checkIsbnExists(isbn);
    }
}
