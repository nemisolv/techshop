package net.nemisolv.techshop.payload.product.request;

public record RetrieveDetailedProductRequest(
        Long id,
        boolean active
) {
}
