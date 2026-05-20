package com.library.biblio.service;

import com.library.biblio.dto.chat.ChatResponse;
import com.library.biblio.entity.Book;
import com.library.biblio.entity.Borrow;
import com.library.biblio.entity.BorrowStatus;
import com.library.biblio.entity.Reservation;
import com.library.biblio.entity.User;
import com.library.biblio.repository.BookRepository;
import com.library.biblio.repository.BorrowRepository;
import com.library.biblio.repository.ReservationRepository;
import com.library.biblio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BorrowRepository borrowRepository;
    private final ReservationRepository reservationRepository;

    private static final List<ChatResponse.Suggestion> DEFAULT_SUGGESTIONS = List.of(
            new ChatResponse.Suggestion("My borrows", "What did I borrow?"),
            new ChatResponse.Suggestion("My reservations", "What did I reserve?"),
            new ChatResponse.Suggestion("Find a book", "How do I search the catalog?"),
            new ChatResponse.Suggestion("Late fees", "How are late fees calculated?")
    );

    @Transactional(readOnly = true)
    public ChatResponse reply(String email, String rawMessage) {
        String msg = rawMessage == null ? "" : rawMessage.toLowerCase(Locale.ROOT).trim();
        User user = userRepository.findByEmailIgnoreCase(email).orElse(null);
        String name = user != null ? user.getFirstName() : "there";

        if (matchesAny(msg, "hi", "hello", "hey", "salut", "bonjour", "ahlan")) {
            return build("greeting",
                    "Hi " + name + "! I'm your library assistant. Ask me about your borrows, reservations, "
                            + "how to find a book, or late fees. How can I help today?",
                    DEFAULT_SUGGESTIONS);
        }

        if (matchesAny(msg, "help", "what can you do", "menu", "commands", "options")) {
            return build("help",
                    "I can help you with:\n"
                            + "• Your active borrows and due dates\n"
                            + "• Your reservations and their status\n"
                            + "• Finding books in the catalog\n"
                            + "• How to reserve or return a book\n"
                            + "• Late fees and library rules\n"
                            + "Try one of the suggestions below.",
                    DEFAULT_SUGGESTIONS);
        }

        if (user != null && matchesAny(msg, "my borrow", "my loan", "what did i borrow", "current loan", "i borrow")) {
            return myBorrowsReply(user);
        }

        if (user != null && matchesAny(msg, "my reservation", "what did i reserve", "i reserved", "pending reservation")) {
            return myReservationsReply(user);
        }

        if (user != null && matchesAny(msg, "due date", "when is due", "return date", "next due", "deadline", "overdue")) {
            return dueDateReply(user);
        }

        if (matchesAny(msg, "how do i reserve", "how to reserve", "reserve a book", "how do i borrow", "how to borrow")) {
            return build("how_reserve",
                    "Open the Books page from the sidebar, search by title, ISBN, author or category, "
                            + "then click Reserve on a book that's available. A librarian will approve your request and "
                            + "you'll get a notification. Maximum 5 active borrows per student.",
                    List.of(new ChatResponse.Suggestion("Open catalog", "How do I search the catalog?")));
        }

        if (matchesAny(msg, "search", "find book", "find a book", "look for", "browse", "catalog", "looking for")) {
            return searchReply(msg);
        }

        if (matchesAny(msg, "fine", "fee", "late", "penalty", "amount due")) {
            return build("fees",
                    "Late fees are 0.50 € per day past the due date. The fee is calculated automatically "
                            + "when a return is registered. You can see any outstanding fee on the 'My Borrows' page.",
                    List.of(new ChatResponse.Suggestion("My borrows", "What did I borrow?")));
        }

        if (matchesAny(msg, "contact", "librarian", "help desk", "support", "phone", "email")) {
            return build("contact",
                    "Librarians can answer questions in person at the front desk, or you can email "
                            + "support@biblio.local. For account issues, use the Profile page to update your details.",
                    null);
        }

        if (matchesAny(msg, "cancel reservation", "cancel my reservation")) {
            return build("cancel_reservation",
                    "Go to 'My Reservations' from the sidebar. Reservations in PENDING status have a Cancel link "
                            + "on the right. Approved reservations can't be cancelled directly — contact a librarian.",
                    null);
        }

        if (matchesAny(msg, "dark mode", "light mode", "theme", "night mode")) {
            return build("theme",
                    "Click the sun/moon icon in the top bar to toggle between light and dark themes. "
                            + "Your choice is remembered across sessions.",
                    null);
        }

        return build("fallback",
                "I'm not sure I got that, " + name + ". I'm best at library-specific questions — try asking "
                        + "about your borrows, your reservations, how to reserve a book, or late fees.",
                DEFAULT_SUGGESTIONS);
    }

    private ChatResponse myBorrowsReply(User user) {
        var page = borrowRepository.findByUserId(user.getId(), PageRequest.of(0, 5));
        if (page.isEmpty()) {
            return build("my_borrows",
                    "You don't have any borrows yet. Head over to the Books page to find something to read!",
                    List.of(new ChatResponse.Suggestion("Find a book", "How do I search the catalog?")));
        }
        StringBuilder sb = new StringBuilder("Here are your recent borrows:\n");
        for (Borrow b : page.getContent()) {
            sb.append("• ").append(b.getBook().getTitle())
                    .append(" — ").append(b.getStatus())
                    .append(", due ").append(b.getDueDate())
                    .append("\n");
        }
        if (page.getTotalElements() > page.getSize()) {
            sb.append("…and ").append(page.getTotalElements() - page.getSize()).append(" more on the 'My Borrows' page.");
        }
        return build("my_borrows", sb.toString().trim(), null);
    }

    private ChatResponse myReservationsReply(User user) {
        var page = reservationRepository.findByUserId(user.getId(), PageRequest.of(0, 5));
        if (page.isEmpty()) {
            return build("my_reservations",
                    "You don't have any reservations right now. You can reserve any available book from the Books page.",
                    null);
        }
        StringBuilder sb = new StringBuilder("Your recent reservations:\n");
        for (Reservation r : page.getContent()) {
            sb.append("• ").append(r.getBook().getTitle())
                    .append(" — ").append(r.getStatus())
                    .append(", made on ").append(r.getReservationDate())
                    .append("\n");
        }
        return build("my_reservations", sb.toString().trim(), null);
    }

    private ChatResponse dueDateReply(User user) {
        var active = borrowRepository.findByUserId(user.getId(), PageRequest.of(0, 50))
                .getContent().stream()
                .filter(b -> b.getStatus() == BorrowStatus.ACTIVE || b.getStatus() == BorrowStatus.OVERDUE)
                .sorted(Comparator.comparing(Borrow::getDueDate))
                .toList();

        if (active.isEmpty()) {
            return build("due_date", "You have no active borrows — no deadlines to worry about.", null);
        }
        Borrow next = active.get(0);
        long days = ChronoUnit.DAYS.between(LocalDate.now(), next.getDueDate());
        String when;
        if (days < 0) when = "overdue by " + (-days) + " day(s)";
        else if (days == 0) when = "due today";
        else when = "due in " + days + " day(s)";
        return build("due_date",
                "Your next return is '" + next.getBook().getTitle() + "' — " + when
                        + " (" + next.getDueDate() + ").",
                List.of(new ChatResponse.Suggestion("My borrows", "What did I borrow?")));
    }

    private ChatResponse searchReply(String msg) {
        String[] tokens = msg.split("\\s+");
        String keyword = Arrays.stream(tokens)
                .filter(t -> t.length() > 3)
                .filter(t -> !Arrays.asList("book", "find", "search", "browse", "catalog",
                        "looking", "about", "with", "that", "have", "want").contains(t))
                .findFirst().orElse(null);

        if (keyword == null) {
            return build("search_help",
                    "Open the Books page from the sidebar. You can filter by category, search by title, ISBN, "
                            + "or author name, and toggle 'Available only' to see what you can reserve right now.",
                    null);
        }

        var page = bookRepository.search(keyword, null, true, PageRequest.of(0, 5));
        if (page.isEmpty()) {
            return build("search_empty",
                    "I couldn't find an available book matching '" + keyword + "'. Try browsing the Books page "
                            + "with a different keyword or category.",
                    null);
        }
        StringBuilder sb = new StringBuilder("I found these books for '").append(keyword).append("':\n");
        for (Book b : page.getContent()) {
            sb.append("• ").append(b.getTitle());
            if (b.getCategory() != null) sb.append(" (").append(b.getCategory().getName()).append(")");
            sb.append(" — ").append(b.getAvailableCopies()).append(" available\n");
        }
        sb.append("Open the Books page to reserve one.");
        return build("search_results", sb.toString().trim(), null);
    }

    private boolean matchesAny(String msg, String... needles) {
        for (String n : needles) if (msg.contains(n)) return true;
        return false;
    }

    private ChatResponse build(String intent, String reply, List<ChatResponse.Suggestion> suggestions) {
        return ChatResponse.builder()
                .intent(intent)
                .reply(reply)
                .suggestions(suggestions)
                .build();
    }
}
