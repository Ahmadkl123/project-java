package com.library.biblio.service;

import com.library.biblio.dto.PageResponse;
import com.library.biblio.dto.book.BookDto;
import com.library.biblio.dto.book.BookRequest;
import com.library.biblio.entity.Author;
import com.library.biblio.entity.Book;
import com.library.biblio.entity.Category;
import com.library.biblio.exception.BadRequestException;
import com.library.biblio.exception.ResourceNotFoundException;
import com.library.biblio.mapper.BookMapper;
import com.library.biblio.repository.AuthorRepository;
import com.library.biblio.repository.BookRepository;
import com.library.biblio.repository.BorrowRepository;
import com.library.biblio.repository.CategoryRepository;
import com.library.biblio.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final AuthorRepository authorRepository;
    private final ReservationRepository reservationRepository;
    private final BorrowRepository borrowRepository;
    private final BookMapper mapper;
    private final AuditService auditService;

    @Transactional(readOnly = true)
    public PageResponse<BookDto> search(String q, Long categoryId, boolean availableOnly, Pageable pageable) {
        Page<Book> page = bookRepository.search(q, categoryId, availableOnly, pageable);
        return PageResponse.of(page.map(mapper::toDto));
    }

    @Transactional(readOnly = true)
    public List<BookDto> recommendForUser(Long userId, int limit) {
        Pageable lookup = PageRequest.of(0, 100);

        Set<Book> historyBooks = new LinkedHashSet<>();
        reservationRepository.findByUserId(userId, lookup).forEach(r -> historyBooks.add(r.getBook()));
        borrowRepository.findByUserId(userId, lookup).forEach(b -> historyBooks.add(b.getBook()));

        Set<Long> excludeIds = historyBooks.stream().map(Book::getId).collect(Collectors.toSet());
        Set<Long> categoryIds = historyBooks.stream()
                .map(Book::getCategory)
                .filter(c -> c != null)
                .map(Category::getId)
                .collect(Collectors.toSet());

        Pageable top = PageRequest.of(0, limit);
        List<Book> recs;
        if (categoryIds.isEmpty()) {
            recs = bookRepository.findRecentAvailable(top);
        } else {
            Set<Long> excludeForQuery = excludeIds.isEmpty() ? Set.of(-1L) : excludeIds;
            recs = bookRepository.findRecommendationsByCategories(categoryIds, excludeForQuery, top);
            if (recs.size() < limit) {
                List<Long> alreadySuggested = recs.stream().map(Book::getId).toList();
                Set<Long> excludeForFill = new HashSet<>(excludeIds);
                excludeForFill.addAll(alreadySuggested);
                int remaining = limit - recs.size();
                bookRepository.findRecentAvailable(PageRequest.of(0, remaining + excludeForFill.size()))
                        .stream()
                        .filter(b -> !excludeForFill.contains(b.getId()))
                        .limit(remaining)
                        .forEach(recs::add);
            }
        }

        return recs.stream().map(mapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public BookDto getById(Long id) {
        return mapper.toDto(findOrThrow(id));
    }

    @Transactional
    public BookDto create(BookRequest req) {
        if (req.getIsbn() != null && !req.getIsbn().isBlank() && bookRepository.existsByIsbn(req.getIsbn())) {
            throw new BadRequestException("ISBN already exists");
        }
        Book book = Book.builder()
                .title(req.getTitle())
                .isbn(req.getIsbn())
                .description(req.getDescription())
                .publisher(req.getPublisher())
                .publicationYear(req.getPublicationYear())
                .language(req.getLanguage())
                .pages(req.getPages())
                .coverUrl(req.getCoverUrl())
                .totalCopies(req.getTotalCopies())
                .availableCopies(req.getTotalCopies())
                .build();
        applyRelations(book, req);
        Book saved = bookRepository.save(book);
        auditService.log("BOOK_CREATE", "Book", saved.getId(), saved.getTitle());
        return mapper.toDto(saved);
    }

    @Transactional
    public BookDto update(Long id, BookRequest req) {
        Book book = findOrThrow(id);
        if (req.getIsbn() != null && !req.getIsbn().equals(book.getIsbn())
                && bookRepository.existsByIsbn(req.getIsbn())) {
            throw new BadRequestException("ISBN already exists");
        }
        book.setTitle(req.getTitle());
        book.setIsbn(req.getIsbn());
        book.setDescription(req.getDescription());
        book.setPublisher(req.getPublisher());
        book.setPublicationYear(req.getPublicationYear());
        book.setLanguage(req.getLanguage());
        book.setPages(req.getPages());
        book.setCoverUrl(req.getCoverUrl());

        int diff = req.getTotalCopies() - book.getTotalCopies();
        book.setTotalCopies(req.getTotalCopies());
        book.setAvailableCopies(Math.max(0, book.getAvailableCopies() + diff));
        applyRelations(book, req);
        auditService.log("BOOK_UPDATE", "Book", id, book.getTitle());
        return mapper.toDto(book);
    }

    @Transactional
    public void delete(Long id) {
        Book book = findOrThrow(id);
        bookRepository.delete(book);
        auditService.log("BOOK_DELETE", "Book", id, book.getTitle());
    }

    private void applyRelations(Book book, BookRequest req) {
        if (req.getCategoryId() != null) {
            Category c = categoryRepository.findById(req.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", req.getCategoryId()));
            book.setCategory(c);
        } else {
            book.setCategory(null);
        }
        Set<Author> authors = new HashSet<>();
        if (req.getAuthorIds() != null) {
            for (Long aid : req.getAuthorIds()) {
                authors.add(authorRepository.findById(aid)
                        .orElseThrow(() -> new ResourceNotFoundException("Author", aid)));
            }
        }
        book.setAuthors(authors);
    }

    Book findOrThrow(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", id));
    }
}
