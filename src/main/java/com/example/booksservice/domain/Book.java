package com.example.booksservice.domain;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Books")
@Data
public class Book {
    private String id;
    private String author;
    private String genre;
    private String publisher;
    private String title;
    private String description;
    private ObjectId imageId;
    private Integer userId;
}






    
   