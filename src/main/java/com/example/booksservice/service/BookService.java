package com.example.booksservice.service;

import com.example.booksservice.client.UserServiceClient;
import com.example.booksservice.domain.Book;
import com.example.booksservice.domain.User;
import com.example.booksservice.exceptions.ImageNotFoundException;
import com.example.booksservice.repository.BooksRepository;
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

    private final UserServiceClient userServiceClient;

    private final GridFsTemplate gridFsTemplate;

    public BookService(BooksRepository booksRepository, UserServiceClient userServiceClient, GridFsTemplate gridFsTemplate) {
        this.booksRepository = booksRepository;
        this.userServiceClient = userServiceClient;
        this.gridFsTemplate = gridFsTemplate;
    }

    public Optional<Book> getBookDetails(String bookId) {
        return booksRepository.findById(bookId);
    }

    @CircuitBreaker(name = "User-service", fallbackMethod = "fallbackGetUser")
    public Optional<User> getUserDetails(Long id) {
        return userServiceClient.getUserById(id);
    }

    public Optional<User> fallbackGetUser(Long id, Throwable throwable) {
        System.err.println("Error getting user with id " + id + ": " + throwable.getMessage());
        return Optional.empty();
    }

    public List<Book> getBooks() {
        return booksRepository.findAll();
    }

    public void update(Book book) {
        booksRepository.save(book);
    }

    public void delete(String id) {
        booksRepository.deleteById(id);
    }

    public Book addBookWithImage(Book book, MultipartFile image) throws IOException {
        ObjectId imageId = gridFsTemplate.store(image.getInputStream(), image.getOriginalFilename(), image.getContentType());
        book.setImageId(imageId);
        return booksRepository.save(book);
    }

    public Optional<Book> getBookWithImage(String bookId) {
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
