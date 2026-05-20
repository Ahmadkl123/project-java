package com.library.biblio.controller;

import com.library.biblio.dto.ApiResponse;
import com.library.biblio.dto.chat.ChatRequest;
import com.library.biblio.dto.chat.ChatResponse;
import com.library.biblio.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Chat", description = "AI library assistant")
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    @Operation(summary = "Send a message to the library assistant")
    public ApiResponse<ChatResponse> message(@AuthenticationPrincipal Object principal,
                                             @Valid @RequestBody ChatRequest request) {
        String email = principal != null ? principal.toString() : null;
        return ApiResponse.ok(chatService.reply(email, request.getMessage()));
    }
}
