package net.nemisolv.techshop.repository;

import net.nemisolv.techshop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE p.name LIKE %:search% OR p.description LIKE %:search%")
    Page<Product> searchProducts(String search, Pageable pageable);

    Optional<Product> findByIdAndActiveTrue(Long productId);
}
