package com.p8io.order_processing_system.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderRequestDTO {
    @NotNull(message = "Order ID is required")
    @Min(value = 1, message = "Order ID must be a positive number")
    private Long orderId;

    @NotBlank(message = "Item name must not be blank")
    private String item;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

}
