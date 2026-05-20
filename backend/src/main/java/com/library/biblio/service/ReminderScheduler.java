package com.library.biblio.service;

import com.library.biblio.entity.Borrow;
import com.library.biblio.entity.BorrowStatus;
import com.library.biblio.entity.NotificationType;
import com.library.biblio.repository.BorrowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReminderScheduler {

    private final BorrowRepository borrowRepository;
    private final NotificationService notificationService;

    @Value("${app.borrow.reminder-days-before}")
    private int reminderDaysBefore;

    @Scheduled(cron = "0 0 8 * * *")
    @Transactional
    public void sendDueDateReminders() {
        LocalDate today = LocalDate.now();
        LocalDate target = today.plusDays(reminderDaysBefore);
        List<Borrow> due = borrowRepository.findActiveDueBetween(today, target);
        log.info("Sending {} due-date reminders", due.size());
        for (Borrow b : due) {
            notificationService.createAndEmail(
                    b.getUser(),
                    NotificationType.DUE_DATE_REMINDER,
                    "Reminder: book due soon",
                    "Your book '" + b.getBook().getTitle() + "' is due on " + b.getDueDate(),
                    "due-reminder",
                    Map.of("user", b.getUser().getFullName(),
                            "book", b.getBook().getTitle(),
                            "dueDate", b.getDueDate()));
        }
    }

    @Scheduled(cron = "0 30 8 * * *")
    @Transactional
    public void flagOverdue() {
        LocalDate today = LocalDate.now();
        List<Borrow> overdue = borrowRepository.findOverdue(BorrowStatus.ACTIVE, today);
        for (Borrow b : overdue) {
            b.setStatus(BorrowStatus.OVERDUE);
            notificationService.createAndEmail(
                    b.getUser(),
                    NotificationType.OVERDUE_ALERT,
                    "Overdue book",
                    "Your borrow of '" + b.getBook().getTitle() + "' is now overdue (was due " + b.getDueDate() + ")",
                    "overdue-alert",
                    Map.of("user", b.getUser().getFullName(),
                            "book", b.getBook().getTitle(),
                            "dueDate", b.getDueDate()));
        }
    }
}
