package net.nemisolv.techshop.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "brands")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Brand extends IdBaseEntity {
    @NotEmpty(message = "Brand name is required")
    private String name;

    private String description;

    @NotEmpty(message = "Brand logo is required")
    private String logoUrl;


}
