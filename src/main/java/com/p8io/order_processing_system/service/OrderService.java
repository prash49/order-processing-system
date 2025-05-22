package com.p8io.order_processing_system.service;

import com.p8io.order_processing_system.dto.OrderRequestDTO;
import com.p8io.order_processing_system.entity.Order;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    Order createOrder(OrderRequestDTO orderRequestDTO);
    List<Order> getAllOrders();
    Optional<Order> getOrderById(Long orderId);

    Order updateOrder(Long orderId, OrderRequestDTO orderRequestDTO);
    void deleteOrder(Long orderId); // Or return boolean/Order if needed
}
