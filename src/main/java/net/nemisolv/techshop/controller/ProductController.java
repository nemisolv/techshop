package net.nemisolv.techshop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.nemisolv.techshop.payload.ApiResponse;
import net.nemisolv.techshop.payload.PagedResponse;
import net.nemisolv.techshop.payload.QueryOption;
import net.nemisolv.techshop.payload.product.request.CreateProductRequest;
import net.nemisolv.techshop.payload.product.request.UpdateProductRequest;
import net.nemisolv.techshop.payload.product.response.ProductOverviewResponse;
import net.nemisolv.techshop.payload.product.response.ProductResponse;
import net.nemisolv.techshop.service.ProductService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ApiResponse<PagedResponse<ProductOverviewResponse>> getProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(defaultValue = "") String searchQuery ) {
        QueryOption queryOption = new QueryOption(page, limit, sortBy, sortDirection, searchQuery, "", true);

        return ApiResponse.success(productService.getProducts(queryOption));
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> getProductById(@PathVariable Long id) {

        return ApiResponse.success(productService.getProductById(id));
    }

    @GetMapping("/categories/{categoryId}")
    public ApiResponse<PagedResponse<ProductOverviewResponse>> getProductsByCategory(@PathVariable Long categoryId,
                                                                                     @RequestParam(defaultValue = "1") int page,
                                                                                     @RequestParam(defaultValue = "10") int limit,
                                                                                     @RequestParam(defaultValue = "id") String sortBy,
                                                                                     @RequestParam(defaultValue = "asc") String sortDirection,
                                                                                     @RequestParam(defaultValue = "") String searchQuery) {
        QueryOption queryOption = new QueryOption(page, limit, sortBy, sortDirection, searchQuery, "", true);
        return ApiResponse.success(productService.getProductsByCategory(categoryId, queryOption));
    }


    @GetMapping("/brands/{brandId}")
    public ApiResponse<PagedResponse<ProductOverviewResponse>> getProductsByBrand(@PathVariable Long brandId,
                                                                                  @RequestParam(defaultValue = "1") int page,
                                                                                  @RequestParam(defaultValue = "10") int limit,
                                                                                  @RequestParam(defaultValue = "id") String sortBy,
                                                                                  @RequestParam(defaultValue = "asc") String sortDirection,
                                                                                  @RequestParam(defaultValue = "") String searchQuery) {
        QueryOption queryOption = new QueryOption(page, limit, sortBy, sortDirection, searchQuery, "", true);
        return ApiResponse.success(productService.getProductsByBrand(brandId, queryOption));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('CREATE_PRODUCT')")
    public ApiResponse<ProductResponse> createProduct(@RequestBody @Valid  CreateProductRequest productRequest) {
        return ApiResponse.success(productService.createProduct(productRequest));
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('UPDATE_PRODUCT')")
    public ApiResponse<ProductResponse> updateProduct(@PathVariable Long id,
                                                      @RequestBody @Valid UpdateProductRequest productRequest) {
        return ApiResponse.success(productService.updateProduct(id, productRequest));
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('DELETE_PRODUCT')")
    public ApiResponse<String> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ApiResponse.success("Product deleted successfully");
    }



}
