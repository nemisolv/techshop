package net.nemisolv.techshop.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nemisolv.techshop.core._enum.PermissionName;
import net.nemisolv.techshop.entity.Product;
import net.nemisolv.techshop.helper.AccessHelper;
import net.nemisolv.techshop.mapper.ProductMapper;
import net.nemisolv.techshop.payload.PagedResponse;
import net.nemisolv.techshop.payload.QueryOption;
import net.nemisolv.techshop.payload.product.request.CreateProductRequest;
import net.nemisolv.techshop.payload.product.request.RetrieveDetailedProductRequest;
import net.nemisolv.techshop.payload.product.response.ProductOverviewResponse;
import net.nemisolv.techshop.payload.product.response.ProductResponse;
import net.nemisolv.techshop.payload.product.request.UpdateProductRequest;
import net.nemisolv.techshop.repository.ProductRepository;
import net.nemisolv.techshop.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public PagedResponse<ProductOverviewResponse> getProducts(QueryOption queryOption) {
        int page = queryOption.page();
        int limit = queryOption.limit();
        String sortBy = queryOption.sortBy();
        String sortDirection = queryOption.sortDirection();
        String search = queryOption.searchQuery();

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page - 1, limit, sort);
        Page<Product> productsPage = null;
        if(StringUtils.hasLength(search) ) {
             productsPage = productRepository.searchProducts(search, pageable);
        }else {
             productsPage = productRepository.findAll(pageable);
        }

        return mapToPagedResponse(productsPage);
    }

    private PagedResponse<ProductOverviewResponse> mapToPagedResponse(Page<Product> productsPage) {
        return  PagedResponse.<ProductOverviewResponse>builder()
                .metadata(
                        productsPage.getContent().stream()
                                .map(productMapper::toOverviewResponse)
                                .toList()
                )
                .pageNo(productsPage.getNumber())
                .limit(productsPage.getSize())
                .totalElements(productsPage.getTotalElements())
                .totalPages(productsPage.getTotalPages())
                .last(productsPage.isLast())
                .build();
    }

    @Override
    public ProductResponse getProductById(RetrieveDetailedProductRequest productRequest) {
        Long productId = productRequest.id();
        boolean isAccessible = AccessHelper.isAccessAllowed(PermissionName.VIEW_SENSITIVE_PRODUCT);
        Product product;
        if(!isAccessible) {
            // means the product is retrieved for the user to view
             product = productRepository.findByIdAndActiveTrue(productId)
                    .orElseThrow(() -> new EntityNotFoundException("Product not found"));
            return productMapper.toResponse(product);
        }
        // means the product is retrieved for the admin to edit
         product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        return productMapper.toResponse(product);

    }

    @Override
    public PagedResponse<ProductOverviewResponse> getProductsByCategory(Long categoryId, QueryOption queryOption) {
        return null;
    }

    @Override
    public PagedResponse<ProductOverviewResponse> getProductsByBrand(Long brandId, QueryOption queryOption) {
        return null;
    }

    @Override
    public ProductResponse createProduct(CreateProductRequest productRequest) {
        return null;
    }

    @Override
    public ProductResponse updateProduct(Long id, UpdateProductRequest productRequest) {
        return null;
    }

    @Override
    public void deleteProduct(Long id) {

    }
}
