package com.library.biblio.mapper;

import com.library.biblio.dto.NotificationDto;
import com.library.biblio.dto.loan.BorrowDto;
import com.library.biblio.dto.loan.ReservationDto;
import com.library.biblio.entity.Borrow;
import com.library.biblio.entity.Notification;
import com.library.biblio.entity.Reservation;
import org.springframework.stereotype.Component;

@Component
public class LoanMapper {

    public ReservationDto toDto(Reservation r) {
        if (r == null) return null;
        return ReservationDto.builder()
                .id(r.getId())
                .userId(r.getUser().getId())
                .userFullName(r.getUser().getFullName())
                .bookId(r.getBook().getId())
                .bookTitle(r.getBook().getTitle())
                .reservationDate(r.getReservationDate())
                .expiryDate(r.getExpiryDate())
                .status(r.getStatus().name())
                .notes(r.getNotes())
                .build();
    }

    public BorrowDto toDto(Borrow b) {
        if (b == null) return null;
        return BorrowDto.builder()
                .id(b.getId())
                .userId(b.getUser().getId())
                .userFullName(b.getUser().getFullName())
                .bookId(b.getBook().getId())
                .bookTitle(b.getBook().getTitle())
                .bookIsbn(b.getBook().getIsbn())
                .borrowDate(b.getBorrowDate())
                .dueDate(b.getDueDate())
                .returnDate(b.getReturnDate())
                .status(b.getStatus().name())
                .fineAmount(b.getFineAmount())
                .notes(b.getNotes())
                .build();
    }

    public NotificationDto toDto(Notification n) {
        if (n == null) return null;
        return NotificationDto.builder()
                .id(n.getId())
                .type(n.getType().name())
                .title(n.getTitle())
                .message(n.getMessage())
                .read(n.isRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
