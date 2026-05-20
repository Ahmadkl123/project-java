package com.library.biblio.controller;

import com.library.biblio.dto.ApiResponse;
import com.library.biblio.dto.PageResponse;
import com.library.biblio.dto.loan.BorrowDto;
import com.library.biblio.dto.loan.BorrowRequest;
import com.library.biblio.dto.user.UserDto;
import com.library.biblio.entity.BorrowStatus;
import com.library.biblio.service.BorrowService;
import com.library.biblio.service.UserService;
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
@RequestMapping("/borrows")
@RequiredArgsConstructor
@Tag(name = "Borrows")
public class BorrowController {

    private final BorrowService service;
    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','BIBLIOTHECAIRE')")
    public ApiResponse<BorrowDto> create(@AuthenticationPrincipal Object principal,
                                         @Valid @RequestBody BorrowRequest req) {
        UserDto me = userService.getByEmail(principal.toString());
        return ApiResponse.ok(service.create(req, me.getId()), "Borrow created");
    }

    @PatchMapping("/{id}/return")
    @PreAuthorize("hasAnyRole('ADMIN','BIBLIOTHECAIRE')")
    public ApiResponse<BorrowDto> returnBook(@PathVariable Long id) {
        return ApiResponse.ok(service.returnBook(id), "Returned");
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<PageResponse<BorrowDto>> mine(@AuthenticationPrincipal Object principal,
                                                     @PageableDefault(size = 10) Pageable pageable) {
        UserDto me = userService.getByEmail(principal.toString());
        return ApiResponse.ok(service.listForUser(me.getId(), pageable));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','BIBLIOTHECAIRE')")
    public ApiResponse<PageResponse<BorrowDto>> listAll(
            @RequestParam(required = false) BorrowStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.ok(service.listAll(status, pageable));
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasAnyRole('ADMIN','BIBLIOTHECAIRE')")
    public ApiResponse<List<BorrowDto>> overdue() {
        return ApiResponse.ok(service.listOverdue());
    }
}
