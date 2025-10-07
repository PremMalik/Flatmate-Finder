package com.roommatefinder.config;

import com.roommatefinder.model.Role;
import com.roommatefinder.model.User;
import com.roommatefinder.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepo;

    @Value("${admin.email}")
    private String adminEmail;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // Load user from Google
        OAuth2User oauthUser = super.loadUser(userRequest);

        // Extract info
        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");
        String picture = oauthUser.getAttribute("picture");
        String googleId = oauthUser.getAttribute("sub");

        // Save if not exists
        userRepo.findByEmail(email).orElseGet(() -> {
            Role role = email.equalsIgnoreCase(adminEmail) ? Role.ADMIN : Role.USER;

            return userRepo.save(
                    User.builder()
                            .googleId(googleId)
                            .name(name)
                            .email(email)
                            .profilePicture(picture)
                            .role(role)
                            .createdAt(Instant.now())
                            .build()
            );
        });

        return oauthUser; // let Spring keep using it
    }
}
