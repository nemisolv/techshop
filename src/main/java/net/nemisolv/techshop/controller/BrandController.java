package net.nemisolv.techshop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.nemisolv.techshop.payload.ApiResponse;
import net.nemisolv.techshop.payload.category.CategoryRequest;
import net.nemisolv.techshop.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ApiResponse<?> getAllCategories() {
        return new ApiResponse<>(true,
                "Categories fetched successfully",
                categoryService.getCategories());
    }

    @GetMapping("/{id}")
    public ApiResponse<?> getCategoryById(@PathVariable Long id) {
        return new ApiResponse<>(true,
                "Category fetched successfully",
                categoryService.getCategory(id));
    }

    @PostMapping
    public ApiResponse<?> addCategory(@RequestBody @Valid CategoryRequest categoryRequest) {
        return new ApiResponse<>(true,
                "Category added successfully",
                categoryService.createCategory(categoryRequest));
    }

    @PutMapping("/{id}")
    public ApiResponse<?> updateCategory(@PathVariable Long id, @RequestBody @Valid CategoryRequest categoryRequest) {
        return new ApiResponse<>(true,
                "Category updated successfully",
                categoryService.updateCategory(id, categoryRequest));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<?> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return  ApiResponse.builder()
                .success(true)
                .message("Category deleted successfully")
                .build();
    }
}
