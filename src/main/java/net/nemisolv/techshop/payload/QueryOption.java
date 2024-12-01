package net.nemisolv.techshop.payload;

public record QueryOption(
        int page,
        int limit,
        String sortBy,
        String sortDirection,
        String searchQuery,
        String filterValue,
        boolean active
) {
}
