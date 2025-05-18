package com.p8io.order_processing_system.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class OrderRequestDTO {
    @NotNull(message = "Required!, Order id must not be null")
    private Long orderId;

    @NotBlank(message = "Item name must not be blank")
    private String item;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    public @NotNull(message = "Required!, Order id must not be null") Long getOrderId() {
        return orderId;
    }

    public OrderRequestDTO setOrderId(@NotNull(message = "Required!, Order id must not be null") Long orderId) {
        this.orderId = orderId;
        return this;
    }

    public @NotBlank(message = "Item name must not be blank") String getItem() {
        return item;
    }

    public OrderRequestDTO setItem(@NotBlank(message = "Item name must not be blank") String item) {
        this.item = item;
        return this;
    }

    public @NotNull(message = "Quantity is required") @Min(value = 1, message = "Quantity must be at least 1") Integer getQuantity() {
        return quantity;
    }

    public OrderRequestDTO setQuantity(@NotNull(message = "Quantity is required") @Min(value = 1, message = "Quantity must be at least 1") Integer quantity) {
        this.quantity = quantity;
        return this;
    }

    @Override
    public String toString() {
        return "OrderRequestDTO{" +
                "orderId=" + orderId +
                ", item='" + item + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
