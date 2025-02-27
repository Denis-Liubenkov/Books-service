package com.example.booksservice.repository;

import com.example.booksservice.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BooksRepository extends JpaRepository<Book, Long> {
    @NonNull
    List<Book> findAll();

    List<Book> findBooksByTitleAndAuthor(String title, String author);

    List<Book> findBooksByGenre(String genre);

    @NonNull
    Optional<Book> findById(@NonNull Long id);

    void deleteById(@NonNull Long id);
}
