package com.example.booksservice.controller;

import com.example.booksservice.domain.Book;
import com.example.booksservice.domain.User;
import com.example.booksservice.exceptions.BookNotFoundException;
import com.example.booksservice.service.BookService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;

import org.springframework.http.*;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

    @GetMapping("/{bookId}")
    public ResponseEntity<Book> getBookById(@PathVariable("bookId") String bookId){
        Book book = bookService.getBookDetails(bookId).orElseThrow(BookNotFoundException::new);
        log.info("Book with id :" + bookId + " is found!");
        return new ResponseEntity<>(book, HttpStatus.OK);
    }

    @GetMapping("/{bookId}/user/{id}")
    public Optional<User> getUserByBookId(@PathVariable String bookId, @PathVariable Long id) {
        return bookService.getUserDetails(id);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Book> uploadBookImage(@Valid @RequestPart("book") Book book, @RequestPart("image") MultipartFile image) throws IOException {
        log.info("Book with id: " + book.getId() + " is created!");
        return ResponseEntity.ok(bookService.addBookWithImage(book, image));
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteBook(@PathVariable("id") String id) {
        bookService.delete(id);
        log.info("Book with id: " + id + " is deleted!");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping
    public ResponseEntity<HttpStatus> updateBook(@RequestBody Book book) {
        bookService.update(book);
        log.info("Book with id: " + book.getId() + " is updated!");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/image/{bookId}")
    public ResponseEntity<InputStreamResource> downloadBookImage(@PathVariable("bookId") String bookId) throws IOException {
        Optional<Book> book = bookService.getBookWithImage(bookId);
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
