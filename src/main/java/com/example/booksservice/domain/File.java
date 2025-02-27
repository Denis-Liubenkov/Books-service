package com.example.booksservice.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Files")
@Data
public class File {
    @Id
    private String id;
    private byte[] data;
    private String filename;
    private String contentType;
}
