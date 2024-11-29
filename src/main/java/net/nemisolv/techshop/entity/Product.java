package net.nemisolv.techshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class Product extends IdBaseEntity {
    private String name;
    private BigDecimal price;
    private String brandId;  // don't store like an object
    private String categoryId;
    private String unit;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String mainImgUrl;
    private Integer quantity;
    private Float averageRating;
    private String videoUrl;
    private String type;

    @Column(columnDefinition = "TEXT")
    private String reviewStory;

}
