package com.library.biblio.repository;

import com.library.biblio.entity.Reservation;
import com.library.biblio.entity.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Page<Reservation> findByUserId(Long userId, Pageable pageable);

    Page<Reservation> findByStatus(ReservationStatus status, Pageable pageable);

    long countByStatus(ReservationStatus status);

    boolean existsByUserIdAndBookIdAndStatusIn(Long userId, Long bookId, java.util.Collection<ReservationStatus> statuses);
}
