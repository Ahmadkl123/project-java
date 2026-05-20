package com.library.biblio.service;

import com.library.biblio.dto.PageResponse;
import com.library.biblio.dto.loan.ReservationDto;
import com.library.biblio.dto.loan.ReservationRequest;
import com.library.biblio.entity.*;
import com.library.biblio.exception.BadRequestException;
import com.library.biblio.exception.ResourceNotFoundException;
import com.library.biblio.mapper.LoanMapper;
import com.library.biblio.repository.BookRepository;
import com.library.biblio.repository.ReservationRepository;
import com.library.biblio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final NotificationService notificationService;
    private final AuditService auditService;
    private final LoanMapper mapper;

    @Transactional
    public ReservationDto create(Long userId, ReservationRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        Book book = bookRepository.findById(req.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book", req.getBookId()));

        if (reservationRepository.existsByUserIdAndBookIdAndStatusIn(userId, book.getId(),
                List.of(ReservationStatus.PENDING, ReservationStatus.APPROVED))) {
            throw new BadRequestException("You already have an active reservation for this book");
        }

        Reservation r = Reservation.builder()
                .user(user).book(book)
                .reservationDate(LocalDate.now())
                .expiryDate(LocalDate.now().plusDays(7))
                .status(ReservationStatus.PENDING)
                .notes(req.getNotes())
                .build();
        Reservation saved = reservationRepository.save(r);

        notificationService.createAndEmail(user, NotificationType.RESERVATION_CREATED,
                "Reservation created",
                "Your reservation for '" + book.getTitle() + "' is pending approval.",
                "reservation-created",
                Map.of("user", user.getFullName(), "book", book.getTitle()));
        auditService.log("RESERVATION_CREATE", "Reservation", saved.getId(), book.getTitle());

        return mapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public PageResponse<ReservationDto> listForUser(Long userId, Pageable pageable) {
        Page<Reservation> page = reservationRepository.findByUserId(userId, pageable);
        return PageResponse.of(page.map(mapper::toDto));
    }

    @Transactional(readOnly = true)
    public PageResponse<ReservationDto> listAll(ReservationStatus status, Pageable pageable) {
        Page<Reservation> page = (status == null)
                ? reservationRepository.findAll(pageable)
                : reservationRepository.findByStatus(status, pageable);
        return PageResponse.of(page.map(mapper::toDto));
    }

    @Transactional
    public ReservationDto updateStatus(Long id, ReservationStatus newStatus) {
        Reservation r = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", id));
        r.setStatus(newStatus);

        NotificationType nt = switch (newStatus) {
            case APPROVED -> NotificationType.RESERVATION_APPROVED;
            case REJECTED -> NotificationType.RESERVATION_REJECTED;
            default -> NotificationType.SYSTEM;
        };
        notificationService.create(r.getUser(), nt,
                "Reservation " + newStatus.name().toLowerCase(),
                "Your reservation for '" + r.getBook().getTitle() + "' was " + newStatus.name().toLowerCase());
        auditService.log("RESERVATION_" + newStatus.name(), "Reservation", id, r.getBook().getTitle());
        return mapper.toDto(r);
    }

    @Transactional
    public void cancel(Long id, Long userId) {
        Reservation r = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", id));
        if (!r.getUser().getId().equals(userId)) {
            throw new BadRequestException("Not your reservation");
        }
        r.setStatus(ReservationStatus.CANCELLED);
        auditService.log("RESERVATION_CANCEL", "Reservation", id, null);
    }
}
