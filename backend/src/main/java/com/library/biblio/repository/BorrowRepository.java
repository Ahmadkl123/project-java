package com.library.biblio.repository;

import com.library.biblio.entity.Borrow;
import com.library.biblio.entity.BorrowStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BorrowRepository extends JpaRepository<Borrow, Long> {

    Page<Borrow> findByUserId(Long userId, Pageable pageable);

    Page<Borrow> findByStatus(BorrowStatus status, Pageable pageable);

    long countByStatus(BorrowStatus status);

    long countByUserIdAndStatus(Long userId, BorrowStatus status);

    @Query("select b from Borrow b where b.status = :status and b.dueDate < :today")
    List<Borrow> findOverdue(@Param("status") BorrowStatus status, @Param("today") LocalDate today);

    @Query("select b from Borrow b where b.status = com.library.biblio.entity.BorrowStatus.ACTIVE " +
            "and b.dueDate between :start and :end")
    List<Borrow> findActiveDueBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);
}
