package com.example.booksservice.domain;

import lombok.Data;

@Data
public class UserClaims {

    private String login;
    private Long userId;

    public UserClaims(String login, Long userId) {
        this.login = login;
        this.userId = userId;
    }
}
