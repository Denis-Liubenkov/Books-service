package com.example.booksservice.client;

import com.example.booksservice.domain.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient(name = "User-service", url = "http://localhost:8081")
public interface UserServiceClient {

    @GetMapping("/users/{id}")
    Optional<User> getUserById(@PathVariable("id") Long id);
}





