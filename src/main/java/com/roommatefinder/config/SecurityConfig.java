package com.roommatefinder.config;

import com.roommatefinder.model.Role;
import com.roommatefinder.model.User;
import com.roommatefinder.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;


import java.time.Instant;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserRepository userRepo;

    @Value("${admin.email}")
    private String adminEmail;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomOAuth2UserService customOAuth2UserService) throws Exception {


        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        // ✅ PUBLIC paths
                        .requestMatchers(
                                 "/","/index.html", "/app.js", "/style.css",            // homepage
                                "/static/**", "/css/**", "/js/**", "/images/**", // assets
                                "/api/ads", "/api/ads/**",       // ads are public
                                "/api/user/me",                  // login check
                                "/oauth2/**"                     // google login
                        ).permitAll()

                        // ✅ everything else needs auth
                        .anyRequest().authenticated()
                )

                .oauth2Login(oauth -> oauth
                        .loginPage("/index.html") // stay on same page
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService)) // ✅ inject service
                        .defaultSuccessUrl("/", true) // always reload homepage
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                );

        return http.build();
    }




    // OPTIONAL: process & save new Google users
    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest) {
        var delegate = new DefaultOAuth2UserService();
        OAuth2User oauthUser = delegate.loadUser(userRequest);

        String email = oauthUser.getAttribute("email");
        String googleId = oauthUser.getAttribute("sub");
        String name = oauthUser.getAttribute("name");
        String picture = oauthUser.getAttribute("picture");

        userRepo.findByEmail(email).orElseGet(() -> {
            Role role = email.equalsIgnoreCase(adminEmail) ? Role.ADMIN : Role.USER;
            return userRepo.save(User.builder()
                    .googleId(googleId)
                    .name(name)
                    .email(email)
                    .profilePicture(picture)
                    .role(role)
                    .createdAt(Instant.now())
                    .build());
        });

        return oauthUser;
    }
}
