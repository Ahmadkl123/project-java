package com.library.biblio.controller;

import com.library.biblio.dto.ApiResponse;
import com.library.biblio.dto.PageResponse;
import com.library.biblio.dto.loan.ReservationDto;
import com.library.biblio.dto.loan.ReservationRequest;
import com.library.biblio.dto.user.UserDto;
import com.library.biblio.entity.ReservationStatus;
import com.library.biblio.service.ReservationService;
import com.library.biblio.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
@Tag(name = "Reservations")
public class ReservationController {

    private final ReservationService service;
    private final UserService userService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<ReservationDto> create(@AuthenticationPrincipal Object principal,
                                              @Valid @RequestBody ReservationRequest req) {
        UserDto me = userService.getByEmail(principal.toString());
        return ApiResponse.ok(service.create(me.getId(), req), "Reservation created");
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<PageResponse<ReservationDto>> mine(@AuthenticationPrincipal Object principal,
                                                          @PageableDefault(size = 10) Pageable pageable) {
        UserDto me = userService.getByEmail(principal.toString());
        return ApiResponse.ok(service.listForUser(me.getId(), pageable));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','BIBLIOTHECAIRE')")
    public ApiResponse<PageResponse<ReservationDto>> listAll(
            @RequestParam(required = false) ReservationStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.ok(service.listAll(status, pageable));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','BIBLIOTHECAIRE')")
    public ApiResponse<ReservationDto> updateStatus(@PathVariable Long id,
                                                    @RequestParam ReservationStatus status) {
        return ApiResponse.ok(service.updateStatus(id, status), "Updated");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Void> cancel(@AuthenticationPrincipal Object principal, @PathVariable Long id) {
        UserDto me = userService.getByEmail(principal.toString());
        service.cancel(id, me.getId());
        return ApiResponse.ok(null, "Cancelled");
    }
}
