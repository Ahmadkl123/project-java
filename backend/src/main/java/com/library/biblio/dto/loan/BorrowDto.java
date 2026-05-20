package com.library.biblio.dto.loan;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowDto {
    private Long id;
    private Long userId;
    private String userFullName;
    private Long bookId;
    private String bookTitle;
    private String bookIsbn;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private String status;
    private Double fineAmount;
    private String notes;
}
