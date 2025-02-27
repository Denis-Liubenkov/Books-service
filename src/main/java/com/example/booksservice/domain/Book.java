package com.example.booksservice.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.bson.types.ObjectId;

@Entity
@Data
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String author;
    private String genre;
    private String publisher;
    private String title;
    private String description;
    private String fileId;
    private ObjectId imageId;
    private Long userId;
}






    
   