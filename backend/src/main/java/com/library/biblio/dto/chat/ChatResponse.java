package com.library.biblio.dto.chat;

import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatResponse {
    private String reply;
    private String intent;
    private List<Suggestion> suggestions;
    @Builder.Default
    private Instant timestamp = Instant.now();

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Suggestion {
        private String label;
        private String value;
    }
}
