import java.util.ArrayList;

/**
 * Created by David on 9/10/2017.
 */
public class BookClass {
    String title;
    String author;
    String publisher;
    String description;
    String isbn;
    Double rating;
    BookClass(String title, String author,String publisher,String description,String isbn,Double rating){
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.description = description;
        this.isbn = isbn;
        this.rating = rating;

    }
    public String toString(){
        return title + author + publisher + description + isbn + Double.toString(rating);
    }
}
