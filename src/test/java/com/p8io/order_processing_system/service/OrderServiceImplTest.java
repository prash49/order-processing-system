package com.p8io.order_processing_system.service;

import com.p8io.order_processing_system.entity.Order;
import com.p8io.order_processing_system.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock
   private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void getOrderById_shouldReturnOrderInOptionalSuccess() {
        Long orderId = 1L;
        Order mockOrder = new Order(orderId, "Dell", 1);
        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));
        Optional<Order> result = orderService.getOrderById(orderId);
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(orderId, result.get().getOrderId());
        assertEquals("Dell", result.get().getItem());
        verify(orderRepository, times(1)).findById(orderId);
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    void getOrderById_shouldReturnEmptyOptionalNotExist(){
        Long  orderId = 21L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
        Optional<Order> result = orderService.getOrderById(orderId);
        assertNotNull(result);
        assertFalse(result.isPresent());
        assertTrue(result.isEmpty());
        verify(orderRepository, times(1)).findById(orderId);
        verifyNoMoreInteractions(orderRepository);
    }  @Test
    void getAllOrders_returnListOfOrders() {
        // Given
        Order order1 = new Order(10L, "Item A", 1);
        Order order2 = new Order(11L, "Item B", 2);
        List<Order> mockOrderList = List.of(order1, order2);
        when(orderRepository.findAll()).thenReturn(mockOrderList);
        List<Order> result = orderService.getAllOrders();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(mockOrderList, result);

        verify(orderRepository, times(1)).findAll();
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    void getAllOrders_returnEmptyList_whenNoOrdersExist() {
        List<Order> emptyList = List.of();
        when(orderRepository.findAll()).thenReturn(emptyList);
        List<Order> result = orderService.getAllOrders();
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(orderRepository, times(1)).findAll();
        verifyNoMoreInteractions(orderRepository);
    }

}
