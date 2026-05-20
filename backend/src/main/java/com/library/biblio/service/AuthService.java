package com.library.biblio.service;

import com.library.biblio.dto.auth.AuthResponse;
import com.library.biblio.dto.auth.LoginRequest;
import com.library.biblio.dto.auth.PasswordChangeRequest;
import com.library.biblio.dto.auth.RegisterRequest;
import com.library.biblio.entity.Role;
import com.library.biblio.entity.RoleName;
import com.library.biblio.entity.User;
import com.library.biblio.exception.BadRequestException;
import com.library.biblio.exception.ResourceNotFoundException;
import com.library.biblio.mapper.UserMapper;
import com.library.biblio.repository.RoleRepository;
import com.library.biblio.repository.UserRepository;
import com.library.biblio.security.JwtTokenProvider;
import com.library.biblio.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtTokenProvider tokenProvider;
    private final UserMapper userMapper;
    private final AuditService auditService;

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmailIgnoreCase(req.getEmail())) {
            throw new BadRequestException("Email already in use");
        }
        if (req.getMatricule() != null && !req.getMatricule().isBlank()
                && userRepository.existsByMatricule(req.getMatricule())) {
            throw new BadRequestException("Matricule already in use");
        }

        Role studentRole = roleRepository.findByName(RoleName.ETUDIANT)
                .orElseThrow(() -> new ResourceNotFoundException("Role ETUDIANT missing — seed data not initialized"));

        Set<Role> roles = new HashSet<>();
        roles.add(studentRole);

        User user = User.builder()
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .email(req.getEmail().toLowerCase())
                .password(passwordEncoder.encode(req.getPassword()))
                .matricule(req.getMatricule())
                .phone(req.getPhone())
                .department(req.getDepartment())
                .enabled(true)
                .roles(roles)
                .build();
        userRepository.save(user);
        auditService.log("USER_REGISTER", "User", user.getId(), user.getEmail());

        return issueToken(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest req) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail().toLowerCase(), req.getPassword()));
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", principal.getId()));
        auditService.log("USER_LOGIN", "User", user.getId(), user.getEmail());
        return issueToken(user);
    }

    @Transactional
    public void changePassword(Long userId, PasswordChangeRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        if (!passwordEncoder.matches(req.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        auditService.log("USER_PASSWORD_CHANGE", "User", user.getId(), null);
    }

    private AuthResponse issueToken(User user) {
        List<String> roles = user.getRoles().stream()
                .map(r -> r.getName().name()).collect(Collectors.toList());
        String token = tokenProvider.generateToken(user.getEmail(), user.getId(), roles);
        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getExpirationMs() / 1000)
                .user(userMapper.toDto(user))
                .build();
    }
}
