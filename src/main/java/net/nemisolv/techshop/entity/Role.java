package net.nemisolv.techshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import net.nemisolv.techshop.core._enum.RoleName;

import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "roles")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@SuperBuilder
public class Role extends IdBaseEntity {

    @Column(nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private RoleName name;
    private String description;
    @ManyToMany
    @JoinTable(
            name = "role_permission", // Tên bảng trung gian
            joinColumns = @JoinColumn(name = "role_id"), // Cột Role
            inverseJoinColumns = @JoinColumn(name = "permission_id") // Cột Permission
    )
    private Set<Permission> permissions ;

    public Role(RoleName name) {
        this.name = name;
    }

    public Role(RoleName name, String description, Set<Permission> permissions) {
        this.name = name;
        this.description = description;
        this.permissions = permissions;
    }
}
