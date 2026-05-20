package com.library.biblio.repository;

import com.library.biblio.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByMatricule(String matricule);

    @Query("select u from User u where " +
            "lower(u.firstName) like lower(concat('%', :q, '%')) or " +
            "lower(u.lastName) like lower(concat('%', :q, '%')) or " +
            "lower(u.email) like lower(concat('%', :q, '%')) or " +
            "lower(coalesce(u.matricule, '')) like lower(concat('%', :q, '%'))")
    Page<User> search(@Param("q") String q, Pageable pageable);

    long countByEnabledTrue();
}
