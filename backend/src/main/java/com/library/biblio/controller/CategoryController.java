package com.library.biblio.controller;

import com.library.biblio.dto.ApiResponse;
import com.library.biblio.dto.book.CategoryDto;
import com.library.biblio.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Tag(name = "Categories")
public class CategoryController {

    private final CategoryService service;

    @GetMapping
    @Operation(summary = "List categories")
    public ApiResponse<List<CategoryDto>> list() {
        return ApiResponse.ok(service.list());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','BIBLIOTHECAIRE')")
    public ApiResponse<CategoryDto> create(@Valid @RequestBody CategoryDto dto) {
        return ApiResponse.ok(service.create(dto), "Created");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','BIBLIOTHECAIRE')")
    public ApiResponse<CategoryDto> update(@PathVariable Long id, @Valid @RequestBody CategoryDto dto) {
        return ApiResponse.ok(service.update(id, dto), "Updated");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.ok(null, "Deleted");
    }
}
