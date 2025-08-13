package com.cs.ecommerce.productservice.service.impl;

import com.cs.ecommerce.productservice.dto.CategoryCreateRequestDTO;
import com.cs.ecommerce.productservice.dto.CategoryDTO;
import com.cs.ecommerce.productservice.entities.Category;
import com.cs.ecommerce.productservice.repository.CategoryRepository;
import com.cs.ecommerce.productservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<CategoryDTO> getAllCategories() {
        List<Category> categories = categoryRepository.findAllActiveNative();
        if (categories.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, List<Category>> childrenGroupedByParentId = categories.stream()
                .filter(c -> c.getParentIdRaw() != null)
                .collect(Collectors.groupingBy(Category::getParentIdRaw));

        return categories.stream()
                .map(category -> {
                    CategoryDTO dto = modelMapper.map(category, CategoryDTO.class);

                    if (category.getParentIdRaw() == null) {
                        dto.setParentId(null);
                        dto.setParentName(null);
                        List<CategoryDTO> childrenDtos = childrenGroupedByParentId
                                .getOrDefault(category.getId(), Collections.emptyList())
                                .stream()
                                .map(child -> {
                                    CategoryDTO childDto = modelMapper.map(child, CategoryDTO.class);
                                    childDto.setChildren(Collections.emptyList());
                                    childDto.setParentId(category.getId());
                                    childDto.setParentName(category.getName());
                                    return childDto;
                                })
                                .toList();
                        dto.setChildren(childrenDtos);
                    } else {
                        dto.setChildren(Collections.emptyList());
                        Category parent = categories.stream()
                                .filter(p -> p.getId().equals(category.getParentIdRaw()))
                                .findFirst()
                                .orElse(null);
                        dto.setParentId(category.getParentIdRaw());
                        dto.setParentName(parent != null ? parent.getName() : null);
                    }
                    return dto;
                })
                .toList();
    }


    @Override
    public CategoryDTO createCategory(CategoryCreateRequestDTO request) {
        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();

        if (request.getParentName() != null && !request.getParentName().isBlank()) {
            Category parent = categoryRepository.findByName(request.getParentName())
                    .orElseThrow(() -> new RuntimeException(
                            "Parent category not found: " + request.getParentName()));

            if (categoryRepository.existsByNameAndParentId(request.getName(), parent.getId())) {
                throw new RuntimeException("Child category name already exists for this parent: " + request.getName());
            }
            category.setParent(parent);
        } else {
            if (categoryRepository.existsByName(request.getName())) {
                throw new RuntimeException("Parent category name already exists: " + request.getName());
            }
        }

        Category saved = categoryRepository.save(category);
        CategoryDTO dto = modelMapper.map(saved, CategoryDTO.class);

        if (saved.getParent() == null) {
            dto.setParentId(null);
            dto.setParentName(null);
        } else {
            dto.setParentId(saved.getParent().getId());
            dto.setParentName(saved.getParent().getName());
        }
        dto.setChildren(Collections.emptyList());

        return dto;
    }

}
