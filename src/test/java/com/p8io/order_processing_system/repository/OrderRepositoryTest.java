package com.p8io.order_processing_system.repository;


import com.p8io.order_processing_system.entity.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application.properties")
public class OrderRepositoryTest {

    @Autowired
    OrderRepository orderRepository;

    @Test
    void testOrderSave(){
        Long orderId = 1L;
        String item = "Dell Laptop";
        Integer quantity = 1;
        Order orderToSave = new Order(orderId,item,quantity);
        Order savedOrder = orderRepository.save(orderToSave);
        assertNotNull(savedOrder);
        assertEquals(orderId, savedOrder.getOrderId());
    }

    @Test
    void testFetchOrderById(){
        orderRepository.save(new Order(1L, "Dell Laptop", 1));
        Long orderId = 1L;
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        assertTrue(orderOptional.isPresent());
        Order foundOrder = orderOptional.get();
        assertEquals(orderId, foundOrder.getOrderId());
    }
    @Test
    void testFindAllOrders(){
        orderRepository.save(new Order(2L, "Mouse", 1));
        orderRepository.save(new Order(3L, "Keyboard", 2));
        orderRepository.save(new Order(4L, "PenDrive", 3));
        List<Order> orders = orderRepository.findAll();
        assertNotNull(orders);
        assertEquals(3,orders.size(), "should find all 3 saved orders");
    }
}
