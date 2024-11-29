package net.nemisolv.techshop.service;

import net.nemisolv.techshop.payload.brand.BrandRequest;
import net.nemisolv.techshop.payload.brand.BrandResponse;

import java.util.List;

public interface BrandService {
    BrandResponse createBrand(BrandRequest request);
    BrandResponse updateBrand(Long id, BrandRequest request);
    void deleteBrand(Long id);
    BrandResponse getBrand(Long id);
    List<BrandResponse> getBrands();

}
