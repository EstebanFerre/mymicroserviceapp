package org.jhipster.mymicroserviceapp.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.jhipster.mymicroserviceapp.service.BookService;
import org.jhipster.mymicroserviceapp.web.rest.util.HeaderUtil;
import org.jhipster.mymicroserviceapp.web.rest.util.PaginationUtil;
import org.jhipster.mymicroserviceapp.service.dto.BookDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Book.
 */
@RestController
@RequestMapping("/api")
public class BookResource {

    private final Logger log = LoggerFactory.getLogger(BookResource.class);
        
    @Inject
    private BookService bookService;

    /**
     * POST  /books : Create a new book.
     *
     * @param bookDTO the bookDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new bookDTO, or with status 400 (Bad Request) if the book has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/books")
    @Timed
    public ResponseEntity<BookDTO> createBook(@Valid @RequestBody BookDTO bookDTO) throws URISyntaxException {
        log.debug("REST request to save Book : {}", bookDTO);
        if (bookDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("book", "idexists", "A new book cannot already have an ID")).body(null);
        }
        BookDTO result = bookService.save(bookDTO);
        return ResponseEntity.created(new URI("/api/books/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("book", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /books : Updates an existing book.
     *
     * @param bookDTO the bookDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated bookDTO,
     * or with status 400 (Bad Request) if the bookDTO is not valid,
     * or with status 500 (Internal Server Error) if the bookDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/books")
    @Timed
    public ResponseEntity<BookDTO> updateBook(@Valid @RequestBody BookDTO bookDTO) throws URISyntaxException {
        log.debug("REST request to update Book : {}", bookDTO);
        if (bookDTO.getId() == null) {
            return createBook(bookDTO);
        }
        BookDTO result = bookService.save(bookDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("book", bookDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /books : get all the books.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of books in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/books")
    @Timed
    public ResponseEntity<List<BookDTO>> getAllBooks(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Books");
        Page<BookDTO> page = bookService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/books");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /books/:id : get the "id" book.
     *
     * @param id the id of the bookDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the bookDTO, or with status 404 (Not Found)
     */
    @GetMapping("/books/{id}")
    @Timed
    public ResponseEntity<BookDTO> getBook(@PathVariable Long id) {
        log.debug("REST request to get Book : {}", id);
        BookDTO bookDTO = bookService.findOne(id);
        return Optional.ofNullable(bookDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /books/:id : delete the "id" book.
     *
     * @param id the id of the bookDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/books/{id}")
    @Timed
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        log.debug("REST request to delete Book : {}", id);
        bookService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("book", id.toString())).build();
    }

    /**
     * SEARCH  /_search/books?query=:query : search for the book corresponding
     * to the query.
     *
     * @param query the query of the book search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/books")
    @Timed
    public ResponseEntity<List<BookDTO>> searchBooks(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Books for query {}", query);
        Page<BookDTO> page = bookService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/books");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
