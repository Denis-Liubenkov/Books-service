package com.example.booksservice.repository;

import com.example.booksservice.domain.Book;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BooksRepository extends MongoRepository<Book,String> {
    @NonNull
    List<Book> findAll();

    @NonNull
    Optional<Book> findById(@NonNull String id);

    void deleteById(@NonNull String id);
}
