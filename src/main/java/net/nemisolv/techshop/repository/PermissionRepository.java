package net.nemisolv.techshop.repository;

import net.nemisolv.techshop.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Integer> {}
