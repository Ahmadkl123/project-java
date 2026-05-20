package com.library.biblio.service;

import com.library.biblio.dto.PageResponse;
import com.library.biblio.dto.loan.BorrowDto;
import com.library.biblio.dto.loan.BorrowRequest;
import com.library.biblio.entity.*;
import com.library.biblio.exception.BadRequestException;
import com.library.biblio.exception.ResourceNotFoundException;
import com.library.biblio.mapper.LoanMapper;
import com.library.biblio.repository.BookRepository;
import com.library.biblio.repository.BorrowRepository;
import com.library.biblio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BorrowService {

    private final BorrowRepository borrowRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final AuditService auditService;
    private final LoanMapper mapper;

    @Value("${app.borrow.default-duration-days}")
    private int defaultDuration;

    @Value("${app.borrow.max-active-borrows}")
    private int maxActive;

    @Transactional
    public BorrowDto create(BorrowRequest req, Long approverId) {
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", req.getUserId()));
        Book book = bookRepository.findById(req.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book", req.getBookId()));
        if (!book.isAvailable()) {
            throw new BadRequestException("Book is not available");
        }
        long active = borrowRepository.countByUserIdAndStatus(user.getId(), BorrowStatus.ACTIVE);
        if (active >= maxActive) {
            throw new BadRequestException("Active borrow limit reached (" + maxActive + ")");
        }
        int duration = (req.getDurationDays() != null && req.getDurationDays() > 0)
                ? req.getDurationDays() : defaultDuration;

        Borrow b = Borrow.builder()
                .user(user).book(book)
                .borrowDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(duration))
                .status(BorrowStatus.ACTIVE)
                .build();
        if (approverId != null) {
            userRepository.findById(approverId).ifPresent(b::setApprovedBy);
        }
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        Borrow saved = borrowRepository.save(b);

        notificationService.createAndEmail(user, NotificationType.BORROW_APPROVED,
                "Borrow approved",
                "You borrowed '" + book.getTitle() + "'. Due " + b.getDueDate(),
                "borrow-approved",
                Map.of("user", user.getFullName(), "book", book.getTitle(), "dueDate", b.getDueDate()));
        auditService.log("BORROW_CREATE", "Borrow", saved.getId(), book.getTitle());
        return mapper.toDto(saved);
    }

    @Transactional
    public BorrowDto returnBook(Long id) {
        Borrow b = borrowRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Borrow", id));
        if (b.getStatus() == BorrowStatus.RETURNED) {
            throw new BadRequestException("Already returned");
        }
        b.setReturnDate(LocalDate.now());
        b.setStatus(BorrowStatus.RETURNED);
        long overdueDays = Math.max(0, ChronoUnit.DAYS.between(b.getDueDate(), LocalDate.now()));
        b.setFineAmount(overdueDays * 0.5);

        Book book = b.getBook();
        book.setAvailableCopies(Math.min(book.getTotalCopies(), book.getAvailableCopies() + 1));

        notificationService.create(b.getUser(), NotificationType.BORROW_RETURNED,
                "Return recorded",
                "Your return of '" + book.getTitle() + "' was recorded.");
        auditService.log("BORROW_RETURN", "Borrow", id, book.getTitle());
        return mapper.toDto(b);
    }

    @Transactional(readOnly = true)
    public PageResponse<BorrowDto> listForUser(Long userId, Pageable pageable) {
        Page<Borrow> page = borrowRepository.findByUserId(userId, pageable);
        return PageResponse.of(page.map(mapper::toDto));
    }

    @Transactional(readOnly = true)
    public PageResponse<BorrowDto> listAll(BorrowStatus status, Pageable pageable) {
        Page<Borrow> page = (status == null)
                ? borrowRepository.findAll(pageable)
                : borrowRepository.findByStatus(status, pageable);
        return PageResponse.of(page.map(mapper::toDto));
    }

    @Transactional(readOnly = true)
    public List<BorrowDto> listOverdue() {
        return borrowRepository.findOverdue(BorrowStatus.ACTIVE, LocalDate.now())
                .stream().map(mapper::toDto).toList();
    }
}
