package com.roommatefinder.controller;

import com.roommatefinder.model.Ad;
import com.roommatefinder.service.AdService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AdController {

    private final AdService adService;

    // ✅ PUBLIC: list all ads
    @GetMapping("/ads")
    public List<Ad> getAllAds() {
        return adService.getAllAds();
    }

    // ✅ LOGIN CHECK
    @GetMapping("/user/me")
    public ResponseEntity<?> getCurrentUser(Authentication auth) {
        if (auth == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Not logged in"));
        }

        if (auth.getPrincipal() instanceof OAuth2User oauthUser) {
            String email = oauthUser.getAttribute("email");
            String name = oauthUser.getAttribute("name");

            String role = email.equals(System.getenv("ADMIN_EMAIL")) ? "ADMIN" : "USER";

            return ResponseEntity.ok(Map.of(
                    "email", email,
                    "name", name,
                    "role", role
            ));
        }

        return ResponseEntity.status(401).body(Map.of("error", "Invalid session"));
    }

    // ✅ AUTH REQUIRED: create ad
    @PostMapping("/ads")
    public Ad createAd(Authentication auth, @RequestBody Ad adRequest) {
        return adService.createAd(auth, adRequest);
    }

    // ✅ AUTH REQUIRED: update ad
    @PutMapping("/ads/{id}")
    public Ad updateAd(Authentication auth, @PathVariable String id, @RequestBody Ad ad) {
        return adService.updateAd(auth, id, ad);
    }

    // ✅ AUTH REQUIRED: delete ad
    @DeleteMapping("/ads/{id}")
    public void deleteAd(Authentication auth, @PathVariable String id) {
        adService.deleteAd(auth, id);
    }
}
