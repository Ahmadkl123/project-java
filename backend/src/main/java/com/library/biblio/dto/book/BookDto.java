package com.library.biblio.dto.book;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookDto {
    private Long id;
    private String title;
    private String isbn;
    private String description;
    private String publisher;
    private Integer publicationYear;
    private String language;
    private Integer pages;
    private String coverUrl;
    private Integer totalCopies;
    private Integer availableCopies;
    private Long categoryId;
    private String categoryName;
    private Set<AuthorRef> authors;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AuthorRef {
        private Long id;
        private String fullName;
    }
}
