package com.example.booksservice.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Order {
    private Long id;
    private Long userId;
    private Long bookId;
    private LocalDateTime orderDate;
}

