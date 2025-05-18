package com.p8io.order_processing_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @Column(nullable = false)
    private Long orderId;
    @Column(nullable = false)
    private String item;
    @Column(nullable = false)
    private int quantity;

    public String getItem() {
        return item;
    }

    public Order setItem(String item) {
        this.item = item;
        return this;
    }

    public int getQuantity() {
        return quantity;
    }

    public Order setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Order setOrderId(Long orderId) {
        this.orderId = orderId;
        return this;
    }
}
