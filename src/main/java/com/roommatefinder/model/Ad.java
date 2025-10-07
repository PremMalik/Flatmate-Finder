package com.roommatefinder.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "ads")
@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class Ad {
    @Id
    private String id;
    private String userId;      // reference to user
    private String userEmail;
    private List<String> images; // Base64 strings
    private int amount;
    private String location;
    private String type;        // ROOMMATE or FLATMATE
    private String description;
    private Instant createdAt;
    private Instant expiresAt;
}
