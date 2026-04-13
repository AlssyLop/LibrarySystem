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
        // El id se pasa como 0 pues la base de datos lo autoincrementa
        bookModel newBook = new bookModel(0, title, isbn, year, idAuthor);
        return bookDAO.registerBook(newBook);
    }

    public bookModel searchBook(int idBook) {
        return bookDAO.searchBook(idBook);
    }

    public List<bookModel> listBooks() {
        return bookDAO.listBooks();
    }
}
