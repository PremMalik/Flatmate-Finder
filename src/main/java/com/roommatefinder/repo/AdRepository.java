package com.roommatefinder.repo;

import com.roommatefinder.model.Ad;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.time.Instant;
import java.util.List;

public interface AdRepository extends MongoRepository<Ad, String> {
    List<Ad> findByExpiresAtBefore(Instant now);
    List<Ad> findByUserId(String userId);
}
