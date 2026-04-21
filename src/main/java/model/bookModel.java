package model;

/**
 *
 * @author Usuario
 */
public class bookModel {
    private int idBook;
    private String title;
    private String isbn;
    private int year;
    private int idAuthor;
    
    // Campo extra para mostrar el nombre en las vistas
    private String authorName;

    public bookModel(int idBook, String title, String isbn, int year, int idAuthor) {
        this.idBook = idBook;
        this.title = title;
        this.isbn = isbn;
        this.year = year;
        this.idAuthor = idAuthor;
    }

    public int getIdBook() {
        return idBook;
    }

    public void setIdBook(int idBook) {
        this.idBook = idBook;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getIdAuthor() {
        return idAuthor;
    }

    public void setIdAuthor(int idAuthor) {
        this.idAuthor = idAuthor;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
}
