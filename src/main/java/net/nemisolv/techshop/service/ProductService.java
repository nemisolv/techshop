package net.nemisolv.techshop.service;

import net.nemisolv.techshop.payload.PagedResponse;
import net.nemisolv.techshop.payload.QueryOption;
import net.nemisolv.techshop.payload.product.request.CreateProductRequest;
import net.nemisolv.techshop.payload.product.request.RetrieveDetailedProductRequest;
import net.nemisolv.techshop.payload.product.response.ProductOverviewResponse;
import net.nemisolv.techshop.payload.product.response.ProductResponse;
import net.nemisolv.techshop.payload.product.request.UpdateProductRequest;

public interface ProductService {
    PagedResponse<ProductOverviewResponse> getProducts(QueryOption queryOption);

    ProductResponse getProductById(Long  id);

    PagedResponse<ProductOverviewResponse> getProductsByCategory(Long categoryId, QueryOption queryOption);

    PagedResponse<ProductOverviewResponse> getProductsByBrand(Long brandId, QueryOption queryOption);

    ProductResponse createProduct(CreateProductRequest productRequest);

    ProductResponse updateProduct(Long id, UpdateProductRequest productRequest);

    void deleteProduct(Long id);

//    void updateStock(Long productId, int quantityDelta);
}
