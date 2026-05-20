package com.library.biblio.dto.loan;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationRequest {
    @NotNull
    private Long bookId;

    @Size(max = 500)
    private String notes;
}
