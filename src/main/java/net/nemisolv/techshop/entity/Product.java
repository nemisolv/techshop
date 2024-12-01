package net.nemisolv.techshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@SuperBuilder
public class Product extends IdBaseEntity {
    private String name;
    private String sku; // Mã sản phẩm
    @Column(columnDefinition = "TEXT")
    private String description;
    private BigDecimal price;
    private String brandId;  // don't store like an object
    private String categoryId;
    private String unit;
    private String mainImgUrl;
    private Integer quantity;
    private Float averageRating;
    private String videoUrl;
    private String type;
    private boolean active; // Trạng thái sản phẩm

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "brand_id", nullable = true) // Optional
    private Brand brand;


    @Column(columnDefinition = "TEXT")
    private String reviewStory;

}
