package com.p8io.order_processing_system.repository;

import com.p8io.order_processing_system.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
