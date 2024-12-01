package net.nemisolv.techshop.payload.product.request;

public record UpdateProductRequest(
        String name,
        String description,
        String mainImgUrl,
        String price,
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
