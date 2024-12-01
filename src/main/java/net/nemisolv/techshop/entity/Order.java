package net.nemisolv.techshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import net.nemisolv.techshop.core._enum.OrderStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_details")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class Order extends IdBaseEntity {


    private String orderNumber;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    private LocalDateTime orderDate;
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;
}
