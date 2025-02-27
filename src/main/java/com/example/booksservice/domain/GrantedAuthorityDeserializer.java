package com.example.booksservice.domain;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GrantedAuthorityDeserializer extends JsonDeserializer<List<GrantedAuthority>> {

    @Override
    public List<GrantedAuthority> deserialize(JsonParser jsonParser, DeserializationContext context)
            throws IOException {
        List<GrantedAuthority> authorities = new ArrayList<>();
        List<Map<String, String>> authList = jsonParser.readValueAs(List.class);
        for (Map<String, String> auth : authList) {
            authorities.add(new SimpleGrantedAuthority(auth.get("authority")));
        }
        return authorities;
    }
}

