package com.library.biblio.service;

import com.library.biblio.entity.AuditLog;
import com.library.biblio.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository repository;

    @Async
    public void log(String action, String entityType, Long entityId, String details) {
        String actor = "anonymous";
        try {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) actor = auth.getName();
        } catch (Exception ignored) {}

        repository.save(AuditLog.builder()
                .actor(actor)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .details(details)
                .timestamp(Instant.now())
                .build());
    }

    public Page<AuditLog> list(Pageable pageable) {
        return repository.findAllByOrderByTimestampDesc(pageable);
    }
}
