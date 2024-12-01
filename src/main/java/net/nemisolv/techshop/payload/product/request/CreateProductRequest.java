package net.nemisolv.techshop.payload.product.request;

import java.math.BigDecimal;

public record CreateProductRequest(
        String name,
        String description,
        String mainImgUrl,
        BigDecimal price,
        String unit,
        Integer quantity,
        String videoUrl,
        String type,
        boolean active,
        String reviewStory,
        Long brandId,
        Long categoryId
) {
}
