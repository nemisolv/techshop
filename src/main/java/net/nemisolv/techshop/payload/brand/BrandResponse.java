package net.nemisolv.techshop.payload.brand;

public record BrandResponse(
        Long id,
        String name,
        String description,
        String logoUrl
) {
}
