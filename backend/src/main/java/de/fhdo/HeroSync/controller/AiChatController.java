package de.fhdo.HeroSync.controller;

import de.fhdo.HeroSync.dto.AiChatRequest;
import de.fhdo.HeroSync.dto.AiChatResponse;
import de.fhdo.HeroSync.entity.User;
import de.fhdo.HeroSync.repository.UserRepository;
import de.fhdo.HeroSync.service.AiChatService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@RequestMapping("/ai")
public class AiChatController {

    private final AiChatService aiChatService;
    private final UserRepository userRepository;

    public AiChatController(AiChatService aiChatService, UserRepository userRepository) {
        this.aiChatService = aiChatService;
        this.userRepository = userRepository;
    }

    @PostMapping(value = "/chat",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AiChatResponse> chat(Authentication auth,
                                               @RequestBody AiChatRequest request) {
        User user = requireUser(auth);
        AiChatResponse response = aiChatService.chat(user, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/diagnostics", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> diagnostics(Authentication auth,
                                              @RequestBody(required = false) AiChatRequest ignored) {
        User user = requireUser(auth);
        AiChatRequest req = new AiChatRequest("Run diagnostics", "diagnostics");
        AiChatResponse response = aiChatService.chat(user, req);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response.getReply());
    }

    private User requireUser(Authentication auth) {
        if (auth == null) throw new ResponseStatusException(UNAUTHORIZED);
        String email = (auth.getPrincipal() instanceof UserDetails ud)
                ? ud.getUsername() : auth.getPrincipal().toString();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED));
    }
}
