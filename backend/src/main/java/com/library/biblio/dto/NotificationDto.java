package com.library.biblio.dto;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDto {
    private Long id;
    private String type;
    private String title;
    private String message;
    private boolean read;
    private Instant createdAt;
}
