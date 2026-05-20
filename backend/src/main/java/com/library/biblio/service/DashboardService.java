package com.library.biblio.service;

import com.library.biblio.dto.DashboardStats;
import com.library.biblio.entity.BorrowStatus;
import com.library.biblio.entity.ReservationStatus;
import com.library.biblio.repository.BookRepository;
import com.library.biblio.repository.BorrowRepository;
import com.library.biblio.repository.ReservationRepository;
import com.library.biblio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BorrowRepository borrowRepository;
    private final ReservationRepository reservationRepository;

    private static final DateTimeFormatter MONTH_FMT = DateTimeFormatter.ofPattern("yyyy-MM");

    @Transactional(readOnly = true)
    public DashboardStats getStats() {
        long totalBooks = bookRepository.count();
        long availableBooks = bookRepository.countByAvailableCopiesGreaterThan(0);
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByEnabledTrue();
        long activeBorrows = borrowRepository.countByStatus(BorrowStatus.ACTIVE);

        long overdueBorrows = borrowRepository
                .findOverdue(BorrowStatus.ACTIVE, LocalDate.now()).size();

        long pendingReservations = reservationRepository.countByStatus(ReservationStatus.PENDING);

        Map<String, Long> byMonth = new HashMap<>();
        YearMonth ym = YearMonth.now();
        for (int i = 5; i >= 0; i--) {
            byMonth.put(ym.minusMonths(i).format(MONTH_FMT), 0L);
        }
        borrowRepository.findAll().forEach(b -> {
            String key = YearMonth.from(b.getBorrowDate()).format(MONTH_FMT);
            byMonth.computeIfPresent(key, (k, v) -> v + 1);
        });

        List<DashboardStats.TopBook> topBooks = borrowRepository.findAll().stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        b -> b.getBook().getId(),
                        java.util.stream.Collectors.counting()))
                .entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(5)
                .map(e -> {
                    var book = bookRepository.findById(e.getKey()).orElse(null);
                    return DashboardStats.TopBook.builder()
                            .bookId(e.getKey())
                            .title(book != null ? book.getTitle() : "—")
                            .borrowCount(e.getValue())
                            .build();
                })
                .toList();

        return DashboardStats.builder()
                .totalBooks(totalBooks)
                .availableBooks(availableBooks)
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .activeBorrows(activeBorrows)
                .overdueBorrows(overdueBorrows)
                .pendingReservations(pendingReservations)
                .borrowsByMonth(byMonth)
                .topBooks(topBooks)
                .build();
    }
}
