package org.example;

import io.quarkus.test.junit.QuarkusTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;
import java.util.Optional;

@QuarkusTest
public class LibraryTest {

    @Inject
    Library library;

    @Test
    void bookFindable() {
        Optional<Book> book = library.byIsbn("9781932394153");
        Assertions.assertThat(book).isNotNull();
    }


}
