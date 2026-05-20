package com.library.biblio.dto.user;

import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String matricule;
    private String phone;
    private String department;
    private LocalDate dateOfBirth;
    private boolean enabled;
    private Set<String> roles;
}
