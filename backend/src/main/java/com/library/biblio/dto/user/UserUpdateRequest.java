package com.library.biblio.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequest {
    @Size(max = 80)
    private String firstName;
    @Size(max = 80)
    private String lastName;
    @Email
    private String email;
    @Size(max = 30)
    private String phone;
    @Size(max = 80)
    private String department;
    private LocalDate dateOfBirth;
    private Boolean enabled;
    private Set<String> roles;
}
