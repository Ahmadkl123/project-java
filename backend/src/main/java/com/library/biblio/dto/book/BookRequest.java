package com.library.biblio.dto.book;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookRequest {

    @NotBlank
    @Size(max = 250)
    private String title;

    @Size(max = 20)
    private String isbn;

    @Size(max = 3000)
    private String description;

    @Size(max = 120)
    private String publisher;

    @Min(1000)
    @Max(3000)
    private Integer publicationYear;

    @Size(max = 30)
    private String language;

    @Positive
    private Integer pages;

    @Size(max = 500)
    private String coverUrl;

    @NotNull
    @Min(0)
    private Integer totalCopies;

    private Long categoryId;

    private Set<Long> authorIds;
}
