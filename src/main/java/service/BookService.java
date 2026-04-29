package service;

import dao.IBookDAO;
import dao.IAuthorDAO;
import dao.impl.BookDAO;
import dao.impl.AuthorDAO;
import model.BookModel;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookService {

    private IBookDAO bookDAO = new BookDAO();
    private IAuthorDAO authorDAO = new AuthorDAO();

    public List<BookModel> listBooksPaginated(int limit, int offset, String query) {
        return bookDAO.listBooksPaginated(limit, offset, query);
    }

    public int countBooks(String query) {
        return bookDAO.countBooks(query);
    }

    public void registerBook(String idBook, String title, String isbn, String year, String idAuthor) throws Exception {
        BookModel book = validateBook(true, idBook, title, isbn, year, idAuthor);
        boolean ok = bookDAO.registerBook(book);
        if (!ok) {
            throw new Exception("Error al registrar el libro.");
        }
    }

    public void updateBook(String idBook, String title, String isbn, String year, String idAuthor) throws Exception {
        BookModel validatedBook = validateBook(false, idBook, title, isbn, year, idAuthor);
        BookModel currentBook = bookDAO.searchBook(validatedBook.getIdBook());

        Map<String, Object> changes = new HashMap<>();
        if (!currentBook.getTitle().equals(validatedBook.getTitle())) {
            changes.put("title", validatedBook.getTitle());
        }
        if (!currentBook.getIsbn().equals(validatedBook.getIsbn())) {
            changes.put("isbn", validatedBook.getIsbn());
        }
        if (currentBook.getYear() != validatedBook.getYear()) {
            changes.put("year", validatedBook.getYear());
        }
        if (currentBook.getIdAuthor() != validatedBook.getIdAuthor()) {
            changes.put("id_author", validatedBook.getIdAuthor());
        }

        if (changes.isEmpty()) {
            throw new Exception("No se detectaron cambios.");
        }

        boolean ok = bookDAO.updateBookPartial(validatedBook.getIdBook(), changes);
        if (!ok) {
            throw new Exception("Error al actualizar el libro.");
        }
    }

    private BookModel validateBook(boolean isRegister, String idBook, String title, String isbn, String year, String idAuthor) throws Exception {
        String message = "";
        try {
            int IDbook = 0;
            if (!isRegister) {
                message = "El libro es requerido.";
                idBook = idBook.replaceAll("\\s+", "");
                messageException(idBook.isBlank(), message);
                message = "Libro inválido";
                IDbook = Integer.parseInt(idBook);
                messageException(IDbook < 1, message);
                messageException(!this.bookDAO.checkIdLibroExits(IDbook), "Libro no válido. No se encuentra registrado.");
            }

            message = "El titulo es requerido.";
            title = String.join(" ", title.trim().split("\\s+")).toUpperCase();
            messageException(title.isBlank(), message);
            String regexTitle = "^[\\p{L}0-9\\s.,!?:;'-]{3,150}$";
            messageException(!title.matches(regexTitle), "Título inválido");
            messageException(title.length() < 3 || title.length() > 150, "Titulo inválido. debe tener entre 3 y 150 caracteres.");

            message = "El ISBN es requerido.";
            isbn = isbn.replaceAll("\\s+", "");
            messageException(isbn.isBlank(), message);
            messageException(isbn.length() < 13 || isbn.length() > 20, "ISBN inválido. Debe tener entre 13 y 20 caracteres.");
            String regexIsbn = "^(?:ISBN(?:-1[03])?:?\\ )?(?=[-0-9]{13}$|[-0-9X]{10}$|[-0-9]{17}$|97[89][-0-9]{13}$)(?:97[89][-0-9]{13}|[0-9]{1,5}[-0-9]+[0-9X])$";
            messageException(!isbn.matches(regexIsbn), "ISBN inválido.");
            
            if (isRegister) {
                messageException(this.bookDAO.checkIsbnExists(isbn), "El ISBN ya existe.");
            } else {
                String currentIsbn = this.bookDAO.searchBook(IDbook).getIsbn();
                if (!currentIsbn.equals(isbn)) {
                    messageException(this.bookDAO.checkIsbnExists(isbn), "Error, el ISBN ingresado " + isbn + " ya se encuentra asignado.");
                }
            }

            message = "El año de publicación es requerido.";
            year = year.replaceAll("\\s+", "");
            messageException(year.isBlank(), message);
            message = "Año de publicación inválido";
            int YearVal = Integer.parseInt(year);
            messageException(YearVal < 1450 || YearVal > LocalDate.now().getYear(), message);

            message = "El autor es requerido.";
            messageException(idAuthor.isBlank(), message);
            message = "Autor no válido";
            messageException(idAuthor.matches(".*\\s.*"), message);
            int IDautor = Integer.parseInt(idAuthor);
            messageException(IDautor < 1, message);
            messageException(!this.authorDAO.checkIdAuthorExits(IDautor), "Autor no válido. No se encuentra registrado");
            
            return new BookModel(IDbook, title, isbn, YearVal, IDautor);

        } catch (NullPointerException | NumberFormatException e) {
            throw new Exception(message);
        }
    }

    private void messageException(boolean condition, String message) throws Exception {
        if (condition) throw new Exception(message);
    }
}
