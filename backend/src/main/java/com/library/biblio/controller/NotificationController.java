package com.library.biblio.controller;

import com.library.biblio.dto.ApiResponse;
import com.library.biblio.dto.NotificationDto;
import com.library.biblio.dto.PageResponse;
import com.library.biblio.dto.user.UserDto;
import com.library.biblio.service.NotificationService;
import com.library.biblio.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Notifications")
public class NotificationController {

    private final NotificationService service;
    private final UserService userService;

    @GetMapping
    public ApiResponse<PageResponse<NotificationDto>> list(@AuthenticationPrincipal Object principal,
                                                            @PageableDefault(size = 20) Pageable pageable) {
        UserDto me = userService.getByEmail(principal.toString());
        return ApiResponse.ok(service.listForUser(me.getId(), pageable));
    }

    @GetMapping("/unread-count")
    public ApiResponse<Map<String, Long>> unreadCount(@AuthenticationPrincipal Object principal) {
        UserDto me = userService.getByEmail(principal.toString());
        return ApiResponse.ok(Map.of("count", service.unreadCount(me.getId())));
    }

    @PatchMapping("/{id}/read")
    public ApiResponse<Void> markRead(@AuthenticationPrincipal Object principal, @PathVariable Long id) {
        UserDto me = userService.getByEmail(principal.toString());
        service.markRead(id, me.getId());
        return ApiResponse.ok(null, "Marked read");
    }

    @PatchMapping("/read-all")
    public ApiResponse<Void> markAllRead(@AuthenticationPrincipal Object principal) {
        UserDto me = userService.getByEmail(principal.toString());
        service.markAllRead(me.getId());
        return ApiResponse.ok(null, "All marked read");
    }
}
