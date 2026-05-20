package com.library.biblio.mapper;

import com.library.biblio.dto.book.AuthorDto;
import com.library.biblio.dto.book.BookDto;
import com.library.biblio.dto.book.CategoryDto;
import com.library.biblio.entity.Author;
import com.library.biblio.entity.Book;
import com.library.biblio.entity.Category;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class BookMapper {

    public BookDto toDto(Book book) {
        if (book == null) return null;
        return BookDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .isbn(book.getIsbn())
                .description(book.getDescription())
                .publisher(book.getPublisher())
                .publicationYear(book.getPublicationYear())
                .language(book.getLanguage())
                .pages(book.getPages())
                .coverUrl(book.getCoverUrl())
                .totalCopies(book.getTotalCopies())
                .availableCopies(book.getAvailableCopies())
                .categoryId(book.getCategory() != null ? book.getCategory().getId() : null)
                .categoryName(book.getCategory() != null ? book.getCategory().getName() : null)
                .authors(book.getAuthors().stream()
                        .map(a -> BookDto.AuthorRef.builder()
                                .id(a.getId())
                                .fullName(a.getFullName())
                                .build())
                        .collect(Collectors.toSet()))
                .build();
    }

    public AuthorDto toDto(Author author) {
        if (author == null) return null;
        return AuthorDto.builder()
                .id(author.getId())
                .firstName(author.getFirstName())
                .lastName(author.getLastName())
                .biography(author.getBiography())
                .nationality(author.getNationality())
                .build();
    }

    public CategoryDto toDto(Category category) {
        if (category == null) return null;
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .bookCount((long) category.getBooks().size())
                .build();
    }
}
