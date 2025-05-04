package org.example;

import jakarta.data.Sort;
import jakarta.data.repository.Delete;
import jakarta.data.repository.Find;
import jakarta.data.repository.Insert;
import jakarta.data.repository.OrderBy;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;
import org.hibernate.annotations.processing.Pattern;

import java.util.List;
import java.util.Optional;

@Repository
public interface Library {

    @Find
    Optional<Book> byIsbn(String isbn);

    @Find
    List<Book> byTitle(@Pattern String title);

    @Insert
    void add(Book book);

    @Delete
    void delete(String isbn);

    @Find
    List<Book> allBooks(Sort<Book> bookSort);

    @Find
    @OrderBy("isbn")
    List<Book> allBooks();

    @Query("""
            select b.isbn, b.title, listagg(a.name, ' & ')
            from Book b join b.authors a
            group by b
            order by b.isbn
            """)
    List<Summary> summarize();

}
