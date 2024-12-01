package net.nemisolv.techshop.repository;

import net.nemisolv.techshop.entity.Inventory;
import net.nemisolv.techshop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
}
