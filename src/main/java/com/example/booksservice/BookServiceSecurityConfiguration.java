package com.example.booksservice;

import com.example.booksservice.filter.BookFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
//@EnableMethodSecurity
public class BookServiceSecurityConfiguration {

    private final BookFilter bookFilter;

    public BookServiceSecurityConfiguration(BookFilter bookFilter) {
        this.bookFilter = bookFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers(HttpMethod.GET, "/books").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/books/**").hasRole("USER")
                                .requestMatchers(HttpMethod.POST, "/books").hasAnyRole("USER", "ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/books/{id}").hasAnyRole("USER", "ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/books/{id}").hasRole("ADMIN")
                                .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(bookFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
