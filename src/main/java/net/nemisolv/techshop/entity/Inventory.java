package net.nemisolv.techshop.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "brands")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class Inventory extends IdBaseEntity {


    @OneToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; // Liên kết với Product

    private int quantity; // Số lượng tồn kho
    private int reservedQuantity; // Số lượng đã đặt
    private int thresholdQuantity; // Ngưỡng tồn kho tối thiểu
    private String warehouseLocation; // Vị trí trong kho
}
