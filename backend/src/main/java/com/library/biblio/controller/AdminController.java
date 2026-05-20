package com.library.biblio.controller;

import com.library.biblio.dto.ApiResponse;
import com.library.biblio.dto.DashboardStats;
import com.library.biblio.entity.AuditLog;
import com.library.biblio.service.AuditService;
import com.library.biblio.service.DashboardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.library.biblio.dto.PageResponse;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','BIBLIOTHECAIRE')")
@Tag(name = "Admin")
public class AdminController {

    private final DashboardService dashboardService;
    private final AuditService auditService;

    @GetMapping("/dashboard")
    public ApiResponse<DashboardStats> dashboard() {
        return ApiResponse.ok(dashboardService.getStats());
    }

    @GetMapping("/audit")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PageResponse<AuditLog>> audit(@PageableDefault(size = 50) Pageable pageable) {
        Page<AuditLog> page = auditService.list(pageable);
        return ApiResponse.ok(PageResponse.of(page));
    }
}
