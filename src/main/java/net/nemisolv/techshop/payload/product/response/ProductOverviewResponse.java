package net.nemisolv.techshop.payload.product.response;

import java.math.BigDecimal;

public record ProductOverviewResponse(
        Long id,
        String name,
        String description,
        String mainImgUrl,
        BigDecimal  price,
        Long brandId,
        Long categoryId
) {
}
