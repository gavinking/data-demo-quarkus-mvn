package org.example;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import java.util.List;

@Path("/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LibraryResource {

    @Inject
    Library library;

    @GET
    public List<Book> allBooks() {
        return library.allBooks(_Book.title.ascIgnoreCase());
    }

    @GET
    @Path("/{isbn}")
    public Book byIsbn(@PathParam String isbn) {
        return library.byIsbn(isbn)
                .orElseThrow(() -> new NotFoundException(isbn));
    }

    @GET
    @Path("/title/{title}")
    public List<Book> byTitle(@PathParam String title) {
        String pattern = '%' + title.replace('*', '%') + '%';
        return library.byTitle(pattern);
    }

    @POST
    @Transactional
    public Response create(Book book) {
        library.add(book);
        return Response.status(Response.Status.CREATED).build();
    }

    @DELETE
    @Path("/{isbn}")
    @Transactional
    public Response delete(@PathParam String isbn) {
        library.delete(isbn);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @GET
    @Path("/summary")
    public List<Summary> summary() {
        return library.summarize();
    }

}
