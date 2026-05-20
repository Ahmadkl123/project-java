package com.library.biblio.repository;

import com.library.biblio.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    boolean existsByIsbn(String isbn);

    @Query("select b from Book b " +
            "where b.category.id in :categoryIds " +
            "and b.id not in :excludeBookIds " +
            "and b.availableCopies > 0 " +
            "order by b.availableCopies desc, b.id desc")
    List<Book> findRecommendationsByCategories(@Param("categoryIds") Collection<Long> categoryIds,
                                               @Param("excludeBookIds") Collection<Long> excludeBookIds,
                                               Pageable pageable);

    @Query("select b from Book b where b.availableCopies > 0 order by b.id desc")
    List<Book> findRecentAvailable(Pageable pageable);

    @Query("select distinct b from Book b " +
            "left join b.authors a " +
            "left join b.category c " +
            "where (:q is null or :q = '' " +
            "      or lower(b.title) like lower(concat('%', :q, '%')) " +
            "      or lower(coalesce(b.isbn, '')) like lower(concat('%', :q, '%')) " +
            "      or lower(concat(a.firstName, ' ', a.lastName)) like lower(concat('%', :q, '%'))) " +
            "and (:categoryId is null or c.id = :categoryId) " +
            "and (:availableOnly = false or b.availableCopies > 0)")
    Page<Book> search(@Param("q") String q,
                      @Param("categoryId") Long categoryId,
                      @Param("availableOnly") boolean availableOnly,
                      Pageable pageable);

    long countByAvailableCopiesGreaterThan(int value);
}
