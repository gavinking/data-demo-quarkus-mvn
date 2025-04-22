package org.example;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

/**
 * Integration test that uses the HTTP client against the local server HTTP API.
 * <p>
 * The test expects that the Quarkus application is running, so make sure to start it before running mvn verify.
 */
class RemoteLibraryIT {

    private static final String REST_TARGET_URL = "http://localhost:8080";

    private String getServerHost() {
        String host = System.getenv("SERVER_HOST");
        if (host == null) {
            host = System.getProperty("server.host");
        }
        return host;
    }

    URL getRequestUrl(String path) {
        String host = getServerHost();
        if (host == null) {
            host = REST_TARGET_URL;
        }
        try {
            return new URI(host + "/books/" + path).toURL();
        } catch (URISyntaxException | MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Test
    public void testCRUD() {

        var books = getBooksFromRestApi();
        var initialSize = books.size();

        assertThat(initialSize, greaterThan(1));

        var randomIsbn = UUID.randomUUID().toString();
        var newBook = new Book(randomIsbn, "The title", "text");

        saveBookToRestApi(newBook);

        books = getBooksFromRestApi();
        assertThat(books.size(), equalTo(initialSize + 1));

        getFromApi(randomIsbn)
                .body("isbn", equalTo(randomIsbn))
                .body("title", equalTo("The title"));

        deleteBook(randomIsbn);

        books = getBooksFromRestApi();
        assertThat(books.size(), equalTo(initialSize));
    }

    private List<Book> getBooksFromRestApi() {
        return getFromApi("")
                .extract()
                .as(new TypeRef<>() {
                });
    }

    private ValidatableResponse getFromApi(String path) {
        return RestAssured
                .given()
                .when().get(getRequestUrl(path))
                .then().statusCode(200);
    }

    private void saveBookToRestApi(Book newBook) {
        RestAssured
                .given().body(newBook).contentType(ContentType.JSON)
                .when().post(getRequestUrl(""))
                .then().statusCode(201);
    }

    private void deleteBook(String isbn) {
        RestAssured
                .given()
                .when().delete(getRequestUrl(isbn))
                .then().statusCode(204);
    }

}