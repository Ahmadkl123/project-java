package com.library.biblio.mapper;

import com.library.biblio.dto.user.UserDto;
import com.library.biblio.entity.Role;
import com.library.biblio.entity.User;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserDto toDto(User user) {
        if (user == null) return null;
        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .matricule(user.getMatricule())
                .phone(user.getPhone())
                .department(user.getDepartment())
                .dateOfBirth(user.getDateOfBirth())
                .enabled(user.isEnabled())
                .roles(user.getRoles().stream().map(Role::getName).map(Enum::name).collect(Collectors.toSet()))
                .build();
    }
}
