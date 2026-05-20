package com.library.biblio.seed;

import com.library.biblio.entity.*;
import com.library.biblio.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        seedRoles();
        seedUsers();
        seedCatalog();
    }

    private void seedRoles() {
        Arrays.stream(RoleName.values()).forEach(rn -> {
            if (roleRepository.findByName(rn).isEmpty()) {
                roleRepository.save(Role.builder().name(rn).build());
                log.info("Seeded role {}", rn);
            }
        });
    }

    private void seedUsers() {
        if (userRepository.count() > 0) return;

        Role admin = roleRepository.findByName(RoleName.ADMIN).orElseThrow();
        Role lib = roleRepository.findByName(RoleName.BIBLIOTHECAIRE).orElseThrow();
        Role student = roleRepository.findByName(RoleName.ETUDIANT).orElseThrow();

        userRepository.save(User.builder()
                .firstName("Admin").lastName("Principal")
                .email("admin@biblio.local")
                .password(passwordEncoder.encode("Admin@123"))
                .enabled(true)
                .roles(new HashSet<>(Set.of(admin, lib)))
                .build());

        userRepository.save(User.builder()
                .firstName("Sarah").lastName("Librarian")
                .email("librarian@biblio.local")
                .password(passwordEncoder.encode("Librarian@123"))
                .department("Library Services")
                .enabled(true)
                .roles(new HashSet<>(Set.of(lib)))
                .build());

        userRepository.save(User.builder()
                .firstName("Mohammed").lastName("Benali")
                .email("librarian2@biblio.local")
                .password(passwordEncoder.encode("Librarian@123"))
                .department("Library Services")
                .phone("+212 600-000001")
                .enabled(true)
                .roles(new HashSet<>(Set.of(lib)))
                .build());

        record StudentSeed(String first, String last, String email, String mat,
                           String dept, String phone, boolean enabled) {}

        List<StudentSeed> students = List.of(
                new StudentSeed("Akram",   "Etudiant", "etudiant@biblio.local",  "E2025-001", "Computer Science",      "+212 600-100001", true),
                new StudentSeed("Yasmine", "El Idrissi","yasmine@biblio.local",  "E2025-002", "Mathematics",           "+212 600-100002", true),
                new StudentSeed("Omar",    "Tazi",     "omar@biblio.local",      "E2025-003", "Physics",               "+212 600-100003", true),
                new StudentSeed("Fatima",  "Zahra",    "fatima@biblio.local",    "E2025-004", "Literature",            "+212 600-100004", true),
                new StudentSeed("Karim",   "Naciri",   "karim@biblio.local",     "E2025-005", "Computer Science",      "+212 600-100005", true),
                new StudentSeed("Salma",   "Benjelloun","salma@biblio.local",    "E2025-006", "Economics",             "+212 600-100006", true),
                new StudentSeed("Youssef", "Amrani",   "youssef@biblio.local",   "E2025-007", "Engineering",           "+212 600-100007", true),
                new StudentSeed("Lina",    "Cherkaoui","lina@biblio.local",      "E2025-008", "Biology",               "+212 600-100008", true),
                new StudentSeed("Hamza",   "Bennani",  "hamza@biblio.local",     "E2025-009", "Computer Science",      "+212 600-100009", true),
                new StudentSeed("Nora",    "Alaoui",   "nora@biblio.local",      "E2025-010", "History",               "+212 600-100010", true),
                new StudentSeed("Reda",    "Saidi",    "reda.inactive@biblio.local","E2025-011","Chemistry",           "+212 600-100011", false)
        );

        for (StudentSeed s : students) {
            userRepository.save(User.builder()
                    .firstName(s.first()).lastName(s.last())
                    .email(s.email())
                    .password(passwordEncoder.encode("Etudiant@123"))
                    .matricule(s.mat())
                    .department(s.dept())
                    .phone(s.phone())
                    .enabled(s.enabled())
                    .roles(new HashSet<>(Set.of(student)))
                    .build());
        }

        log.info("Seeded {} users (1 admin / 2 librarians / {} students)", userRepository.count(), students.size());
    }

    private void seedCatalog() {
        if (bookRepository.count() > 0) return;

        Category cs  = saveCategory("Computer Science", "Algorithms, software, AI");
        Category math = saveCategory("Mathematics", "Pure and applied math");
        Category phy = saveCategory("Physics", "Classical and modern physics");
        Category lit = saveCategory("Literature", "Novels, poetry, drama");
        Category his = saveCategory("History", "World, regional, social history");

        Author knuth = saveAuthor("Donald", "Knuth", "Pioneer of analysis of algorithms.", "American");
        Author hopcroft = saveAuthor("John", "Hopcroft", "Co-author of foundational CS texts.", "American");
        Author tao = saveAuthor("Terence", "Tao", "Fields medal mathematician.", "Australian");
        Author rowling = saveAuthor("J. K.", "Rowling", "British novelist.", "British");
        Author hugo = saveAuthor("Victor", "Hugo", "French romantic writer.", "French");

        saveBook("The Art of Computer Programming", "9780201896831", cs, Set.of(knuth), 5,
                "Comprehensive monograph on algorithms.", "Addison-Wesley", 1968, "English", 650);
        saveBook("Introduction to Automata Theory", "9780321455369", cs, Set.of(hopcroft), 3,
                "Foundational textbook on automata, languages, computation.", "Pearson", 2006, "English", 535);
        saveBook("Analysis I", "9789380250649", math, Set.of(tao), 4,
                "Rigorous introductory real analysis.", "Hindustan Book Agency", 2009, "English", 368);
        saveBook("Les Miserables", "9782070409228", lit, Set.of(hugo), 2,
                "Classic French novel.", "Gallimard", 1862, "French", 1463);
        saveBook("Harry Potter and the Philosopher's Stone", "9780747532699", lit, Set.of(rowling), 6,
                "First book in the Harry Potter series.", "Bloomsbury", 1997, "English", 223);
        saveBook("Concrete Mathematics", "9780201558029", math, Set.of(knuth), 3,
                "Foundation for computer science.", "Addison-Wesley", 1994, "English", 657);
        saveBook("A Brief History of Time", "9780553380163", phy, Set.of(), 4,
                "Popular cosmology.", "Bantam", 1988, "English", 256);
        saveBook("Sapiens", "9780099590088", his, Set.of(), 5,
                "A brief history of humankind.", "Vintage", 2014, "English", 443);

        log.info("Seeded catalog: {} books across {} categories", bookRepository.count(), categoryRepository.count());
    }

    private Category saveCategory(String name, String desc) {
        return categoryRepository.save(Category.builder().name(name).description(desc).build());
    }

    private Author saveAuthor(String first, String last, String bio, String nat) {
        return authorRepository.save(Author.builder()
                .firstName(first).lastName(last).biography(bio).nationality(nat).build());
    }

    private void saveBook(String title, String isbn, Category cat, Set<Author> authors, int copies,
                          String desc, String publisher, int year, String lang, int pages) {
        String coverUrl = (isbn != null && !isbn.isBlank())
                ? "https://covers.openlibrary.org/b/isbn/" + isbn + "-L.jpg"
                : null;
        bookRepository.save(Book.builder()
                .title(title)
                .isbn(isbn)
                .description(desc)
                .publisher(publisher)
                .publicationYear(year)
                .language(lang)
                .pages(pages)
                .coverUrl(coverUrl)
                .totalCopies(copies)
                .availableCopies(copies)
                .category(cat)
                .authors(new HashSet<>(authors))
                .build());
    }
}
