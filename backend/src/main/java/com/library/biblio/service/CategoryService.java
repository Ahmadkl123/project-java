package com.library.biblio.service;

import com.library.biblio.dto.book.CategoryDto;
import com.library.biblio.entity.Category;
import com.library.biblio.exception.BadRequestException;
import com.library.biblio.exception.ResourceNotFoundException;
import com.library.biblio.mapper.BookMapper;
import com.library.biblio.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository repository;
    private final BookMapper mapper;

    @Transactional(readOnly = true)
    public List<CategoryDto> list() {
        return repository.findAll().stream().map(mapper::toDto).toList();
    }

    @Transactional
    public CategoryDto create(CategoryDto dto) {
        if (repository.existsByNameIgnoreCase(dto.getName())) {
            throw new BadRequestException("Category name already exists");
        }
        Category c = Category.builder().name(dto.getName()).description(dto.getDescription()).build();
        return mapper.toDto(repository.save(c));
    }

    @Transactional
    public CategoryDto update(Long id, CategoryDto dto) {
        Category c = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
        c.setName(dto.getName());
        c.setDescription(dto.getDescription());
        return mapper.toDto(c);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) throw new ResourceNotFoundException("Category", id);
        repository.deleteById(id);
    }
}
