package com.library.biblio.controller;

import com.library.biblio.dto.ApiResponse;
import com.library.biblio.dto.PageResponse;
import com.library.biblio.dto.book.BookDto;
import com.library.biblio.dto.book.BookRequest;
import com.library.biblio.dto.user.UserDto;
import com.library.biblio.service.BookService;
import com.library.biblio.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@Tag(name = "Books", description = "Catalog operations")
public class BookController {

    private final BookService service;
    private final UserService userService;

    @GetMapping
    @Operation(summary = "Search books with filters")
    public ApiResponse<PageResponse<BookDto>> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "false") boolean availableOnly,
            @PageableDefault(size = 20, sort = "title") Pageable pageable) {
        return ApiResponse.ok(service.search(q, categoryId, availableOnly, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get book details")
    public ApiResponse<BookDto> get(@PathVariable Long id) {
        return ApiResponse.ok(service.getById(id));
    }

    @GetMapping("/recommended")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Books similar to ones the current user has reserved or borrowed")
    public ApiResponse<List<BookDto>> recommended(@AuthenticationPrincipal Object principal,
                                                  @RequestParam(defaultValue = "8") int limit) {
        UserDto me = userService.getByEmail(principal.toString());
        return ApiResponse.ok(service.recommendForUser(me.getId(), limit));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','BIBLIOTHECAIRE')")
    @Operation(summary = "Create a new book")
    public ApiResponse<BookDto> create(@Valid @RequestBody BookRequest req) {
        return ApiResponse.ok(service.create(req), "Created");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','BIBLIOTHECAIRE')")
    @Operation(summary = "Update a book")
    public ApiResponse<BookDto> update(@PathVariable Long id, @Valid @RequestBody BookRequest req) {
        return ApiResponse.ok(service.update(id, req), "Updated");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a book")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.ok(null, "Deleted");
    }
}
