package com.library.biblio.controller;

import com.library.biblio.dto.ApiResponse;
import com.library.biblio.dto.PageResponse;
import com.library.biblio.dto.user.UserDto;
import com.library.biblio.dto.user.UserUpdateRequest;
import com.library.biblio.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users")
public class UserController {

    private final UserService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','BIBLIOTHECAIRE')")
    public ApiResponse<PageResponse<UserDto>> search(
            @RequestParam(required = false) String q,
            @PageableDefault(size = 20, sort = "lastName") Pageable pageable) {
        return ApiResponse.ok(service.search(q, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','BIBLIOTHECAIRE') or @userSecurity.isSelf(#id, principal)")
    public ApiResponse<UserDto> get(@PathVariable Long id) {
        return ApiResponse.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isSelf(#id, principal)")
    public ApiResponse<UserDto> update(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest req,
                                       Authentication authentication) {
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) {
            req.setRoles(null);
            req.setEnabled(null);
        }
        return ApiResponse.ok(service.update(id, req), "Updated");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.ok(null, "Deleted");
    }
}
