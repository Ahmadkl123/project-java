package com.library.biblio.repository;

import com.library.biblio.entity.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    @Query("select a from Author a where :q is null or :q = '' " +
            "or lower(concat(a.firstName, ' ', a.lastName)) like lower(concat('%', :q, '%'))")
    Page<Author> search(@Param("q") String q, Pageable pageable);
}
