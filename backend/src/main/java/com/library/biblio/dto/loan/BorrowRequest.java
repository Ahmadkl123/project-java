package com.library.biblio.dto.loan;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowRequest {
    @NotNull
    private Long userId;

    @NotNull
    private Long bookId;

    @Positive
    private Integer durationDays;
}
