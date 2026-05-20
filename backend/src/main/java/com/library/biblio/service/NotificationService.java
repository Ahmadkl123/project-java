package com.library.biblio.service;

import com.library.biblio.dto.NotificationDto;
import com.library.biblio.dto.PageResponse;
import com.library.biblio.entity.Notification;
import com.library.biblio.entity.NotificationType;
import com.library.biblio.entity.User;
import com.library.biblio.exception.ResourceNotFoundException;
import com.library.biblio.mapper.LoanMapper;
import com.library.biblio.repository.NotificationRepository;
import com.library.biblio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final LoanMapper mapper;

    @Transactional
    public Notification create(User user, NotificationType type, String title, String message) {
        return notificationRepository.save(Notification.builder()
                .user(user).type(type).title(title).message(message).build());
    }

    @Transactional
    public Notification createAndEmail(User user, NotificationType type, String title, String message,
                                       String template, Map<String, Object> vars) {
        Notification saved = create(user, type, title, message);
        emailService.sendHtml(user.getEmail(), title, template, vars);
        saved.setEmailSent(true);
        return saved;
    }

    @Transactional(readOnly = true)
    public PageResponse<NotificationDto> listForUser(Long userId, Pageable pageable) {
        Page<Notification> page = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return PageResponse.of(page.map(mapper::toDto));
    }

    @Transactional(readOnly = true)
    public long unreadCount(Long userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    @Transactional
    public void markRead(Long id, Long userId) {
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", id));
        if (!n.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Notification", id);
        }
        n.setRead(true);
    }

    @Transactional
    public void markAllRead(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", userId));
        notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, Pageable.unpaged())
                .forEach(n -> n.setRead(true));
    }
}
