package com.library.biblio.service;

import com.library.biblio.dto.PageResponse;
import com.library.biblio.dto.book.AuthorDto;
import com.library.biblio.entity.Author;
import com.library.biblio.exception.ResourceNotFoundException;
import com.library.biblio.mapper.BookMapper;
import com.library.biblio.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository repository;
    private final BookMapper mapper;

    @Transactional(readOnly = true)
    public PageResponse<AuthorDto> search(String q, Pageable pageable) {
        Page<Author> page = repository.search(q == null ? "" : q, pageable);
        return PageResponse.of(page.map(mapper::toDto));
    }

    @Transactional
    public AuthorDto create(AuthorDto dto) {
        Author a = Author.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .biography(dto.getBiography())
                .nationality(dto.getNationality())
                .build();
        return mapper.toDto(repository.save(a));
    }

    @Transactional
    public AuthorDto update(Long id, AuthorDto dto) {
        Author a = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author", id));
        a.setFirstName(dto.getFirstName());
        a.setLastName(dto.getLastName());
        a.setBiography(dto.getBiography());
        a.setNationality(dto.getNationality());
        return mapper.toDto(a);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) throw new ResourceNotFoundException("Author", id);
        repository.deleteById(id);
    }
}
