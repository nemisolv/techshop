package net.nemisolv.techshop.repository;

import net.nemisolv.techshop.core._enum.RoleName;
import net.nemisolv.techshop.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(RoleName role);
}
