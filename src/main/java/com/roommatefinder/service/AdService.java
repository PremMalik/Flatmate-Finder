package com.roommatefinder.service;

import com.roommatefinder.model.Ad;
import com.roommatefinder.model.Role;
import com.roommatefinder.model.User;
import com.roommatefinder.repo.AdRepository;
import com.roommatefinder.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdService {

    private final AdRepository adRepo;
    private final UserRepository userRepo;

    public List<Ad> getAllAds() {
        return adRepo.findAll().stream()
                .filter(ad -> ad.getExpiresAt().isAfter(Instant.now()))
                .toList();
    }

    public Ad createAd(Authentication auth, Ad adRequest) {
        String email = getEmailFromAuth(auth);
        User user = userRepo.findByEmail(email).orElseThrow();

        // ensure user has no active ad
        List<Ad> userAds = adRepo.findByUserId(user.getId());
        boolean hasActive = userAds.stream().anyMatch(ad -> ad.getExpiresAt().isAfter(Instant.now()));
        if (hasActive) throw new RuntimeException("You already have an active ad!");

        adRequest.setUserId(user.getId());
        adRequest.setUserEmail(email);
        adRequest.setCreatedAt(Instant.now());
        adRequest.setExpiresAt(Instant.now().plusSeconds(30L * 24 * 60 * 60));
        return adRepo.save(adRequest);
    }

    public Ad updateAd(Authentication auth, String adId, Ad updatedAd) {
        Ad existing = adRepo.findById(adId).orElseThrow();
        if (!canEdit(auth, existing)) throw new RuntimeException("Not allowed");

        existing.setAmount(updatedAd.getAmount());
        existing.setLocation(updatedAd.getLocation());
        existing.setType(updatedAd.getType());
        existing.setDescription(updatedAd.getDescription());
        existing.setImages(updatedAd.getImages());
        if (updatedAd.getImages() != null && !updatedAd.getImages().isEmpty()) {
            existing.setImages(updatedAd.getImages());
        }
        return adRepo.save(existing);
    }

    public void deleteAd(Authentication auth, String adId) {
        Ad existing = adRepo.findById(adId).orElseThrow();
        if (!canEdit(auth, existing)) throw new RuntimeException("Not allowed");
        adRepo.delete(existing);
    }

    private boolean canEdit(Authentication auth, Ad ad) {
        String email = getEmailFromAuth(auth);
        User user = userRepo.findByEmail(email).orElseThrow();
        return user.getRole() == Role.ADMIN || ad.getUserId().equals(user.getId());
    }

    private String getEmailFromAuth(Authentication auth) {
        if (auth.getPrincipal() instanceof OAuth2User oauthUser) {
            return oauthUser.getAttribute("email");
        }
        throw new RuntimeException("Not logged in with Google!");
    }
}
