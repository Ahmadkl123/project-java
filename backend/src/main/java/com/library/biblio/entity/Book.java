package com.library.biblio.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "books", indexes = {
        @Index(name = "idx_books_isbn", columnList = "isbn", unique = true),
        @Index(name = "idx_books_title", columnList = "title")
})
public class Book extends BaseEntity {

    @Column(name = "title", nullable = false, length = 250)
    private String title;

    @Column(name = "isbn", unique = true, length = 20)
    private String isbn;

    @Column(name = "description", length = 3000)
    private String description;

    @Column(name = "publisher", length = 120)
    private String publisher;

    @Column(name = "publication_year")
    private Integer publicationYear;

    @Column(name = "language", length = 30)
    private String language;

    @Column(name = "pages")
    private Integer pages;

    @Column(name = "cover_url", length = 500)
    private String coverUrl;

    @Column(name = "total_copies", nullable = false)
    @Builder.Default
    private Integer totalCopies = 1;

    @Column(name = "available_copies", nullable = false)
    @Builder.Default
    private Integer availableCopies = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "book_authors",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    @Builder.Default
    private Set<Author> authors = new HashSet<>();

    public boolean isAvailable() {
        return availableCopies != null && availableCopies > 0;
    }
}
