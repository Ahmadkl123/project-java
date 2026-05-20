package com.library.biblio.controller;

import com.library.biblio.dto.ApiResponse;
import com.library.biblio.dto.PageResponse;
import com.library.biblio.dto.book.AuthorDto;
import com.library.biblio.service.AuthorService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authors")
@RequiredArgsConstructor
@Tag(name = "Authors")
public class AuthorController {

    private final AuthorService service;

    @GetMapping
    public ApiResponse<PageResponse<AuthorDto>> search(
            @RequestParam(required = false) String q,
            @PageableDefault(size = 20, sort = "lastName") Pageable pageable) {
        return ApiResponse.ok(service.search(q, pageable));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','BIBLIOTHECAIRE')")
    public ApiResponse<AuthorDto> create(@Valid @RequestBody AuthorDto dto) {
        return ApiResponse.ok(service.create(dto), "Created");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','BIBLIOTHECAIRE')")
    public ApiResponse<AuthorDto> update(@PathVariable Long id, @Valid @RequestBody AuthorDto dto) {
        return ApiResponse.ok(service.update(id, dto), "Updated");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.ok(null, "Deleted");
    }
}
