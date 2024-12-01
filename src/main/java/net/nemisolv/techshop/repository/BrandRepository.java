package net.nemisolv.techshop.repository;

import jakarta.validation.constraints.NotEmpty;
import net.nemisolv.techshop.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Long> {
    Optional<Brand> findByNameIgnoreCase( String name);

    Optional<Brand> findByName(String name);

//    Optional<Brand
}
