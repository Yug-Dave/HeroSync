package de.fhdo.HeroSync.service;

import de.fhdo.HeroSync.dto.LoginRequest;
import de.fhdo.HeroSync.dto.AuthResponse;
import de.fhdo.HeroSync.dto.RegisterRequest;
import de.fhdo.HeroSync.entity.User;
import de.fhdo.HeroSync.entity.Profile;
import de.fhdo.HeroSync.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        User user = new User(request.getName(), request.getEmail(), passwordEncoder.encode(request.getPassword()));
        user.setXp(0);
        user.setEmailVerified(true);
        
        Profile profile = new Profile(user, "Welcome to HeroSync!", "");
        user.setProfile(profile);

        User saved = userRepository.save(user);
        return new AuthResponse(saved.getUserId(), saved.getName(), saved.getEmail());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return new AuthResponse(user.getUserId(), user.getName(), user.getEmail());
    }

    /**
     * Authenticates a user and initiates a session.
     */
    public AuthResponse loginAndCreateSession(LoginRequest request) {
        return login(request);
    }
}
