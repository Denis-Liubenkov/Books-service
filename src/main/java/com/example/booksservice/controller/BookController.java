package com.example.booksservice.controller;

import com.example.booksservice.domain.Book;
import com.example.booksservice.service.BookService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    private static final Logger log = LoggerFactory.getLogger(BookController.class);

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    //@PreAuthorize("hasRole('USER')")
    @GetMapping("/{bookId}")
    public ResponseEntity<Book> getBookById(@PathVariable("bookId") Long bookId, @RequestHeader("Authorization") String token) {
        return bookService.getBookById(bookId, token)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //@PreAuthorize("hasRole('USER')")
    @GetMapping("/user/{id}")
    public ResponseEntity<Book> getBookByUserId(@PathVariable("id") Long id, @RequestHeader("Authorization") String token) {
        Optional<Book> book = bookService.getBooksByUserId(id, token);
        return book.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    //@PreAuthorize("hasRole('USER')")
    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBooks(@RequestParam(required = false) String title,
                                                  @RequestParam(required = false) String author, @RequestHeader("Authorization") String token) {
        Optional<List<Book>> books = Optional.ofNullable(bookService.searchBooks(title, author, token));
        return books.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    //@PreAuthorize("hasRole('USER')")
    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<Book>> getBooksByGenre(@PathVariable String genre, @RequestHeader("Authorization") String token) {
        Optional<List<Book>> books = Optional.ofNullable(bookService.getBooksByGenre(genre, token));
        return books.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    //@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Book> uploadBookImage(@Valid @RequestPart("book") Book book, @RequestPart("image") MultipartFile image, @RequestParam("id") Long id, @RequestHeader("Authorization") String token) throws IOException {
        bookService.addBookWithImage(book, image, id, token);
        log.info("Book with id: " + book.getId() + " is created!");
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/file/{fileId}")
    public ResponseEntity<byte[]> getFile(@PathVariable("fileId") String fileId, @RequestHeader("Authorization") String token) {
        byte[] fileData = bookService.getFile(fileId, token);
        log.info("File " + Arrays.toString(fileData) + " is found!");
        return ResponseEntity.ok(fileData);
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = bookService.getBooks();
        if (books.isEmpty()) {
            log.info("List of books are not found!");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            log.info("List of books are found!");
            return new ResponseEntity<>(books, HttpStatus.OK);
        }
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteBook(@PathVariable("id") Long id) {
        bookService.delete(id);
        log.info("Book with id: " + id + " is deleted!");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @PutMapping("/{id}/{bookId}")
    public ResponseEntity<HttpStatus> updateBook(@RequestBody Book book, @PathVariable("bookId") Long bookId, @PathVariable("id") Long id, @RequestHeader("Authorization") String token) {
        bookService.update(book, id, token);
        log.info("Book with id: " + bookId + " is updated!");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //@PreAuthorize("hasRole('USER')")
    @GetMapping("/image/{bookId}")
    public ResponseEntity<InputStreamResource> downloadBookImage(@PathVariable("bookId") Long bookId, @RequestHeader("Authorization") String token) throws IOException {
        Optional<Book> book = bookService.getBookWithImage(bookId, token);
        if (book.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        GridFsResource resource = bookService.getImage(book.get().getImageId());
        if (resource == null) {
            return ResponseEntity.notFound().build();
        }
        ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                .filename(URLEncoder.encode(resource.getFilename(), StandardCharsets.UTF_8), StandardCharsets.UTF_8)
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(contentDisposition);
        headers.setContentType(MediaType.valueOf(resource.getContentType()));
        return ResponseEntity.ok()
                .headers(headers)
                .body(new InputStreamResource(resource.getInputStream()));
    }
}
