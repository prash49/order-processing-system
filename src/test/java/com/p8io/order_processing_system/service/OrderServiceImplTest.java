package com.p8io.order_processing_system.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.p8io.order_processing_system.dto.OrderRequestDTO;
import com.p8io.order_processing_system.entity.Order;
import com.p8io.order_processing_system.exceptions.OrderAlreadyExistsException;
import com.p8io.order_processing_system.messaging.KafkaProducer;
import com.p8io.order_processing_system.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private KafkaProducer kafkaProducer;

    @Mock
    MessageSource messageSource;

    private String orderPushTopic = "test-order-queue";

    private ObjectMapper testObjectMapper = new ObjectMapper();

    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(orderService, "orderPushTopic", orderPushTopic);
    }

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
    void getOrderById_shouldReturnEmptyOptionalNotExist() {
        Long orderId = 21L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
        Optional<Order> result = orderService.getOrderById(orderId);
        assertNotNull(result);
        assertFalse(result.isPresent());
        assertTrue(result.isEmpty());
        verify(orderRepository, times(1)).findById(orderId);
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
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

    @Test
    void createOrder_saveAndSendToKafka_whenOrderDoesNotExist() throws ExecutionException, InterruptedException, JsonProcessingException, ExecutionException {
        Long orderId = 1L;
        String item = "Dell Laptop";
        Integer quantity = 1;
        OrderRequestDTO orderRequestDTO = new OrderRequestDTO();
        orderRequestDTO.setOrderId(orderId);
        orderRequestDTO.setItem(item);
        orderRequestDTO.setQuantity(quantity);

        Order orderToSave = new Order();
        orderToSave.setOrderId(orderId);
        orderToSave.setItem(item);
        orderToSave.setQuantity(quantity);

        Order savedOrder = new Order(orderId, item, quantity);
        String simulatedOrderJson = testObjectMapper.writeValueAsString(savedOrder);
        when(orderRepository.existsById(orderId)).thenReturn(false);
        when(orderRepository.saveAndFlush(any(Order.class))).thenReturn(savedOrder);
        Order createdOrder = orderService.createOrder(orderRequestDTO);
        assertNotNull(createdOrder);
        assertEquals(orderId, createdOrder.getOrderId());
        assertEquals(item, createdOrder.getItem());
        assertEquals(quantity, createdOrder.getQuantity());

        verify(orderRepository, times(1)).existsById(orderId);
        verify(orderRepository, times(1)).saveAndFlush(any(Order.class));
       verify(kafkaProducer, times(1)).send(eq(orderPushTopic), eq(orderId.toString()), anyString());
        verifyNoMoreInteractions(orderRepository);
        verifyNoMoreInteractions(kafkaProducer);
    }
    @Test
    void createOrder_shouldThrowOrderAlreadyExistsException_whenOrderExists() throws ExecutionException, InterruptedException {
        Long orderId = 1L;
        OrderRequestDTO orderRequestDTO = new OrderRequestDTO();
        orderRequestDTO.setOrderId(orderId);
        when(orderRepository.existsById(orderId)).thenReturn(true);
        when(messageSource.getMessage(eq("order.already.exists"), eq(new Object[]{orderId}), eq(Locale.ENGLISH))).thenReturn("Simulated Localized Message: Order already exists");
        OrderAlreadyExistsException thrown = assertThrows(OrderAlreadyExistsException.class, () -> {
            orderService.createOrder(orderRequestDTO);
        }, "Should throw OrderAlreadyExistsException when order with ID exists");
        assertEquals("Simulated Localized Message: Order already exists", thrown.getMessage());
        verify(orderRepository, times(1)).existsById(orderId);
        verify(messageSource    , times(1)).getMessage(eq("order.already.exists"), eq(new Object[]{orderId}), eq(Locale.ENGLISH));
        verify(orderRepository, never()).saveAndFlush(any(Order.class));
        verify(kafkaProducer, never()).send(anyString(), anyString(), anyString());
        verifyNoMoreInteractions(orderRepository);
        verifyNoMoreInteractions(messageSource);
        verifyNoInteractions(kafkaProducer);
    }


}
