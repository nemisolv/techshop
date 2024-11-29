package net.nemisolv.techshop.service.impl;

import lombok.RequiredArgsConstructor;
import net.nemisolv.techshop.entity.Category;
import net.nemisolv.techshop.mapper.CategoryMapper;
import net.nemisolv.techshop.payload.category.CategoryRequest;
import net.nemisolv.techshop.payload.category.CategoryResponse;
import net.nemisolv.techshop.repository.CategoryRepository;
import net.nemisolv.techshop.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper mapper;


    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = mapper.toEntity(request);
        // check if category already exists
        categoryRepository.findByNameIgnoreCase(category.getName())
                .ifPresent(c -> {
                    throw new IllegalArgumentException("Category already exists");
                });
        return mapper.toResponse(categoryRepository.save(category));
    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Optional<Category> categoryOptional = categoryRepository.findById(id);
        if(categoryOptional.isPresent()) {
            Category category = categoryOptional.get();
            category.setName(request.name());
            if(StringUtils.hasLength(request.description())) {
                category.setDescription(request.description());
            }
            return mapper.toResponse(categoryRepository.save(category));
        } else {
            throw new IllegalArgumentException("Category not found");
        }
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);

    }

    @Override
    public CategoryResponse getCategory(Long id) {
        return categoryRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
    }

    @Override
    public List<CategoryResponse> getCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }
}
