package com.library.biblio.service;

import com.library.biblio.dto.PageResponse;
import com.library.biblio.dto.user.UserDto;
import com.library.biblio.dto.user.UserUpdateRequest;
import com.library.biblio.entity.Role;
import com.library.biblio.entity.RoleName;
import com.library.biblio.entity.User;
import com.library.biblio.exception.BadRequestException;
import com.library.biblio.exception.ResourceNotFoundException;
import com.library.biblio.mapper.UserMapper;
import com.library.biblio.repository.RoleRepository;
import com.library.biblio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final AuditService auditService;

    @Transactional(readOnly = true)
    public PageResponse<UserDto> search(String q, Pageable pageable) {
        Page<User> page = userRepository.search(q == null ? "" : q, pageable);
        return PageResponse.of(page.map(userMapper::toDto));
    }

    @Transactional(readOnly = true)
    public UserDto getById(Long id) {
        return userMapper.toDto(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public UserDto getByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .map(userMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("User", email));
    }

    @Transactional
    public UserDto update(Long id, UserUpdateRequest req) {
        User user = findOrThrow(id);
        if (req.getFirstName() != null) user.setFirstName(req.getFirstName());
        if (req.getLastName() != null) user.setLastName(req.getLastName());
        if (req.getEmail() != null && !req.getEmail().equalsIgnoreCase(user.getEmail())) {
            if (userRepository.existsByEmailIgnoreCase(req.getEmail()))
                throw new BadRequestException("Email already in use");
            user.setEmail(req.getEmail().toLowerCase());
        }
        if (req.getPhone() != null) user.setPhone(req.getPhone());
        if (req.getDepartment() != null) user.setDepartment(req.getDepartment());
        if (req.getDateOfBirth() != null) user.setDateOfBirth(req.getDateOfBirth());
        if (req.getEnabled() != null) user.setEnabled(req.getEnabled());
        if (req.getRoles() != null && !req.getRoles().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            for (String r : req.getRoles()) {
                roles.add(roleRepository.findByName(RoleName.valueOf(r))
                        .orElseThrow(() -> new BadRequestException("Unknown role: " + r)));
            }
            user.setRoles(roles);
        }
        auditService.log("USER_UPDATE", "User", id, null);
        return userMapper.toDto(user);
    }

    @Transactional
    public void delete(Long id) {
        User user = findOrThrow(id);
        userRepository.delete(user);
        auditService.log("USER_DELETE", "User", id, user.getEmail());
    }

    private User findOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }
}
