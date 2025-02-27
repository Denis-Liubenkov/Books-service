package com.example.booksservice.client;

import com.example.booksservice.domain.Order;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Optional;

@FeignClient(name = "Order-service", url = "http://localhost:8083")
public interface OrderServiceClient {

    @GetMapping("/orders/{id}")
    Optional<Order> getOrderById(@PathVariable("id") Long id, @RequestHeader("Authorization") String token);

    @GetMapping("/orders/{id}/{bookId}")
    Optional<Order> getOrderByUserIdAndBookId(@PathVariable("id") Long id, @PathVariable("bookId") Long bookId, @RequestHeader("Authorization") String token);
}


