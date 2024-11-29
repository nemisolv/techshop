package net.nemisolv.techshop.service;

import net.nemisolv.techshop.payload.category.CategoryRequest;
import net.nemisolv.techshop.payload.category.CategoryResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest request);
    CategoryResponse updateCategory(Long id, CategoryRequest request);
    void deleteCategory(Long id);
    CategoryResponse getCategory(Long id);
    List<CategoryResponse> getCategories();


}
