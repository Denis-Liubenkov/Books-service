package com.example.booksservice.repository;

import com.example.booksservice.domain.File;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileRepository extends MongoRepository<File, String> {

    @NonNull
    Optional<File> findById(@NonNull String id);
}
