package com.example.booksservice.client;

import com.example.booksservice.domain.CustomUserDetails;
import com.example.booksservice.domain.UserClaims;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "Authentication", url = "http://localhost:8084")
public interface AuthenticationServiceClient {

    @GetMapping("/auth/validate")
    Boolean validateToken(@RequestHeader("Authorization") String token);

    @GetMapping("/auth/getLoginFromJwt")
    String getLoginFromJwt(@RequestHeader("Authorization") String token);

    @GetMapping("/auth/userDetails/{username}")
    CustomUserDetails loadUserByUsername(@PathVariable String username);

    @GetMapping("/auth/getUserClaimsFromJwt")
    UserClaims getUserClaimsFromJwt(@RequestHeader("Authorization") String token);

    @GetMapping("/auth/removePrefixBearer")
    String removePrefixBearer(@RequestHeader("Authorization") String token);
}
