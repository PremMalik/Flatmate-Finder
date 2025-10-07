package com.roommatefinder.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document(collection = "users")
@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class User {
    @Id
    private String id;
    private String googleId;
    private String name;
    private String email;
    private String profilePicture;
    private Role role;
    private Instant createdAt;
}
