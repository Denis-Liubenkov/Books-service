package com.example.booksservice.exceptions;

public class BookNotFoundException extends RuntimeException{
        public BookNotFoundException() {
            super("Book is not found");
        }
    }

