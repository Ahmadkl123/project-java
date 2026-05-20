package com.library.biblio.security;

import com.library.biblio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("userSecurity")
@RequiredArgsConstructor
public class UserSecurity {

    private final UserRepository userRepository;

    public boolean isSelf(Long targetUserId, Object principal) {
        if (principal == null || targetUserId == null) return false;
        String email = principal.toString();
        return userRepository.findByEmailIgnoreCase(email)
                .map(u -> u.getId().equals(targetUserId))
                .orElse(false);
    }
}
