package com.example.booksservice.domain;

import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate creationDate;
    private Role role;
    private Long orderId;
    private Long bookId;
}
