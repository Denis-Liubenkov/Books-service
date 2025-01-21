package com.example.booksservice.domain;

import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    private Long id;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate creationDate;
    private Integer bookId;
}
