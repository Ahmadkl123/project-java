package com.library.biblio.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "authors")
public class Author extends BaseEntity {

    @Column(name = "first_name", nullable = false, length = 80)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 80)
    private String lastName;

    @Column(name = "biography", length = 2000)
    private String biography;

    @Column(name = "nationality", length = 60)
    private String nationality;

    @ManyToMany(mappedBy = "authors")
    @Builder.Default
    @ToString.Exclude
    private Set<Book> books = new HashSet<>();

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
