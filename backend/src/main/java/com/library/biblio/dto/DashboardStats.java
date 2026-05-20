package com.library.biblio.dto;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStats {
    private long totalBooks;
    private long availableBooks;
    private long totalUsers;
    private long activeUsers;
    private long activeBorrows;
    private long overdueBorrows;
    private long pendingReservations;
    private Map<String, Long> borrowsByMonth;
    private List<TopBook> topBooks;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TopBook {
        private Long bookId;
        private String title;
        private long borrowCount;
    }
}
