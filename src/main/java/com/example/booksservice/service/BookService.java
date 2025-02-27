package com.example.booksservice.service;

import com.example.booksservice.client.AuthenticationServiceClient;
import com.example.booksservice.client.UserServiceClient;
import com.example.booksservice.domain.Book;
import com.example.booksservice.domain.File;
import com.example.booksservice.domain.User;
import com.example.booksservice.exceptions.BookNotFoundException;
import com.example.booksservice.exceptions.ImageNotFoundException;
import com.example.booksservice.exceptions.UserNotFoundException;
import com.example.booksservice.repository.BooksRepository;
import com.example.booksservice.repository.FileRepository;
import com.mongodb.client.gridfs.model.GridFSFile;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.data.mongodb.core.query.Query;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
public class BookService {

    private final BooksRepository booksRepository;

    private final FileRepository fileRepository;
    private final UserServiceClient userServiceClient;

    private final GridFsTemplate gridFsTemplate;

    private final AuthenticationServiceClient authenticationServiceClient;

    public BookService(BooksRepository booksRepository, FileRepository fileRepository, UserServiceClient userServiceClient, GridFsTemplate gridFsTemplate, AuthenticationServiceClient authenticationServiceClient) {
        this.booksRepository = booksRepository;
        this.fileRepository = fileRepository;
        this.userServiceClient = userServiceClient;
        this.gridFsTemplate = gridFsTemplate;
        this.authenticationServiceClient = authenticationServiceClient;
    }

    public Optional<Book> getBookById(Long bookId, String token) {
        if (!authenticationServiceClient.validateToken(token)) {
            throw new RuntimeException("Access denied: No access to book");
        }
        return booksRepository.findById(bookId);
    }

    @CircuitBreaker(name = "books-service", fallbackMethod = "fallbackGetBooksByUserId")
    public Optional<Book> getBooksByUserId(Long id, String token) {
        if (!authenticationServiceClient.validateToken(token)) {
            throw new RuntimeException("Access denied: No access to book by userId");
        }
        Optional<User> userOptional = userServiceClient.getUserById(id, token);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException();
        }
        User user = userOptional.get();
        Long bookId = user.getBookId();
        if (bookId == null) {
            return Optional.empty();
        }
        Optional<Book> book = booksRepository.findById(bookId);
        if (book.isEmpty()) {
            throw new BookNotFoundException();
        }
        return book;
    }

    public List<Book> searchBooks(String title, String author, String token) {
        if (!authenticationServiceClient.validateToken(token)) {
            throw new RuntimeException("Access denied: No access to search books");
        }
        return booksRepository.findBooksByTitleAndAuthor(title, author);
    }

    public List<Book> getBooksByGenre(String genre, String token) {
        if (!authenticationServiceClient.validateToken(token)) {
            throw new RuntimeException("Access denied: No access to search of books by genre");
        }
        return booksRepository.findBooksByGenre(genre);
    }

    public Optional<Book> fallbackGetBooksByUserId(Long id, String token, Throwable throwable) {
        System.err.println("Error getting book by userId " + id + ": " + throwable.getMessage());
        return Optional.empty();
    }

    public List<Book> getBooks() {
        return booksRepository.findAll();
    }

    public void update(Book book, Long id, String token) {
        if (!authenticationServiceClient.validateToken(token)) {
            throw new RuntimeException("Access denied: No access to update book");
        }
        Optional<User> userById = userServiceClient.getUserById(id, token);
        if (userById.isPresent()) {
            User user = userById.get();
            book.setUserId(user.getId());
        } else {
            throw new UserNotFoundException();
        }
        book.setId(book.getId());
        book.setAuthor(book.getAuthor());
        book.setDescription(book.getDescription());
        book.setGenre(book.getGenre());
        book.setPublisher(book.getPublisher());
        book.setTitle(book.getTitle());
        book.setFileId(book.getFileId());
        book.setImageId(book.getImageId());
        booksRepository.save(book);
    }

    public void delete(Long id) {
        booksRepository.deleteById(id);
    }

    public void addBookWithImage(Book book, MultipartFile image, Long id, String token) throws IOException {
        if (!authenticationServiceClient.validateToken(token)) {
            throw new RuntimeException("Access denied: No access to add book with image");
        }
        Optional<User> userOptional = userServiceClient.getUserById(id, token);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException();
        }
        User user = userOptional.get();
        book.setUserId(user.getId());
        ObjectId imageId = gridFsTemplate.store(image.getInputStream(), image.getOriginalFilename(), image.getContentType());
        book.setImageId(imageId);
        book.setFileId(imageId.toString());
        booksRepository.save(book);
        user.setBookId(book.getId());
        userServiceClient.updateUser(user, id, token);
    }

    public byte[] getFile(String fileId, String token) {
        if (!authenticationServiceClient.validateToken(token)) {
            throw new RuntimeException("Access denied: No access to get book with image");
        }
        File file = fileRepository.findById(fileId).orElse(null);
        return (file != null) ? file.getData() : null;
    }

    public Optional<Book> getBookWithImage(Long bookId, String token) {
        if (!authenticationServiceClient.validateToken(token)) {
            throw new RuntimeException("Access denied: No access to get book with image");
        }
        return booksRepository.findById(bookId);
    }

    public GridFsResource getImage(ObjectId imageId) {
        try {
            GridFSFile gridFsFile = gridFsTemplate.findOne(new Query(where("_id").is(imageId)));
            return new GridFsResource(gridFsFile);
        } catch (IllegalArgumentException e) {
            throw new ImageNotFoundException("Invalid image ID: " + imageId, e);
        }
    }
}
