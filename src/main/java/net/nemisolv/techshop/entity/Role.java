package net.nemisolv.techshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "roles")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, unique = true)
    private String name;
    private String description;
    @ManyToMany
    @JoinTable(
            name = "role_permission", // Tên bảng trung gian
            joinColumns = @JoinColumn(name = "role_id"), // Cột Role
            inverseJoinColumns = @JoinColumn(name = "permission_id") // Cột Permission
    )
    private Set<Permission> permissions ;
}
