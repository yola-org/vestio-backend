package org.app.vestiobackend.controller;

import lombok.RequiredArgsConstructor;
import org.app.vestiobackend.dto.CategoryDto;
import org.app.vestiobackend.entity.Category;
import org.app.vestiobackend.mapper.CategoryMapper;
import org.app.vestiobackend.repository.CategoryRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @GetMapping
    @Cacheable("categories")
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        List<Category> allCategories = categoryRepository.findAll();
        List<CategoryDto> dtos = allCategories.stream()
                .map(categoryMapper::toDto)
                .toList();

        List<CategoryDto> hierarchy = dtos.stream()
                .filter(dto -> dto.getParentId() == null)
                .peek(root -> root.setSubCategories(
                        dtos.stream()
                                .filter(sub -> root.getId().equals(sub.getParentId()))
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(hierarchy);
    }
}
