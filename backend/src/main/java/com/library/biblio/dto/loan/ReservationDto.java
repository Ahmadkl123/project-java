package com.library.biblio.dto.loan;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDto {
    private Long id;
    private Long userId;
    private String userFullName;
    private Long bookId;
    private String bookTitle;
    private LocalDate reservationDate;
    private LocalDate expiryDate;
    private String status;
    private String notes;
}
