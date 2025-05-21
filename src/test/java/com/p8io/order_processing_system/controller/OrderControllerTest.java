package com.p8io.order_processing_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.p8io.order_processing_system.dto.OrderRequestDTO;
import com.p8io.order_processing_system.entity.Order;
import com.p8io.order_processing_system.exceptions.OrderAlreadyExistsException;
import com.p8io.order_processing_system.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;


import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith({SpringExtension.class})
@ContextConfiguration(classes = {OrderController.class})
@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Mock
    private OrderService orderService;

    @Mock
    private MessageSource messageSource;

    @Autowired
    private ObjectMapper objectMapper;

    @Configuration
    static class TestConfig {
        @Bean
        public OrderService orderService(@Mock OrderService orderService) {
            return orderService;
        }

        @Bean
        public MessageSource messageSource(@Mock MessageSource messageSource) {
            return messageSource;
        }
    }

    @Autowired
    private OrderService mockedOrderService;

    @Autowired
    private MessageSource mockedMessageSource;


    private void mockLocalizedMessage(String code, String message) {
        when(mockedMessageSource.getMessage(eq(code), any(), any(Locale.class))).thenReturn(message);
    }



    @Test
    void createOrder_shouldReturnCreatedWithApiResponse_whenOrderIsNew() throws Exception {
        Long orderId = 1L;
        String item = "Laptop";
        Integer quantity = 1;
        OrderRequestDTO orderRequestDTO = new OrderRequestDTO();
        orderRequestDTO.setOrderId(orderId);
        orderRequestDTO.setItem(item);
        orderRequestDTO.setQuantity(quantity);

        Order createdOrder = new Order(orderId, item, quantity);
        when(mockedOrderService.createOrder(any(OrderRequestDTO.class))).thenReturn(createdOrder);
        mockLocalizedMessage("order.created.successfully", "Order created successfully message");
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequestDTO)))

                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is("SUCCESS")))
                .andExpect(jsonPath("$.statusCode", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.message", is("Order created successfully message")))
                .andExpect(jsonPath("$.data.orderId", is(orderId.intValue())))
                .andExpect(jsonPath("$.data.item", is(item)))
                .andExpect(jsonPath("$.data.quantity", is(quantity)));

        verify(mockedOrderService, times(1)).createOrder(any(OrderRequestDTO.class));
        verify(mockedMessageSource, times(1)).getMessage(eq("order.created.successfully"), any(), any(Locale.class));
    }

    @Test
    void createOrder_shouldReturnConflict_whenOrderAlreadyExists() throws Exception {
        Long orderId = 2L;
        OrderRequestDTO orderRequestDTO = new OrderRequestDTO();
        orderRequestDTO.setOrderId(orderId);

        when(mockedOrderService.createOrder(any(OrderRequestDTO.class))).thenThrow(new OrderAlreadyExistsException("Order already exists message from service"));

        mockLocalizedMessage("order.already.exists", "Localized error message: Order already exists");
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequestDTO)))
                .andExpect(status().isConflict());

        verify(mockedOrderService, times(1)).createOrder(any(OrderRequestDTO.class));
        verify(mockedMessageSource, times(1)).getMessage(eq("order.already.exists"), any(), any(Locale.class));
    }


    @Test
    void getAllOrders_shouldReturnOkWithListOfOrdersInApiResponse() throws Exception {
        Order order1 = new Order(10L, "Item A", 1);
        Order order2 = new Order(11L, "Item B", 2);
        List<Order> allOrders = Arrays.asList(order1, order2);

        when(mockedOrderService.getAllOrders()).thenReturn(allOrders);
        mockLocalizedMessage("orders.fetched.successfully", "All orders fetched successfully message");

        mockMvc.perform(get("/api/v1/orders").contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is("SUCCESS")))
                .andExpect(jsonPath("$.statusCode", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.message", is("All orders fetched successfully message")))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].orderId", is(10)))
                .andExpect(jsonPath("$.data[0].item", is("Item A")))
                .andExpect(jsonPath("$.data[1].orderId", is(11)))
                .andExpect(jsonPath("$.data[1].item", is("Item B")));

        verify(mockedOrderService, times(1)).getAllOrders();
        verify(mockedMessageSource, times(1)).getMessage(eq("orders.fetched.successfully"), any(), any(Locale.class));
    }

    @Test
    void getAllOrders_shouldReturnOkWithEmptyListInApiResponse_whenNoOrdersExist() throws Exception {
        List<Order> emptyList = List.of();

        when(mockedOrderService.getAllOrders()).thenReturn(emptyList);
        mockLocalizedMessage("orders.fetched.successfully", "All orders fetched successfully message");

        mockMvc.perform(get("/api/v1/orders").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is("SUCCESS")))
                .andExpect(jsonPath("$.statusCode", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.message", is("All orders fetched successfully message")))
                .andExpect(jsonPath("$.data", empty()));
        verify(mockedOrderService, times(1)).getAllOrders();
        verify(mockedMessageSource, times(1)).getMessage(eq("orders.fetched.successfully"), any(), any(Locale.class));
    }



    @Test
    void getOrderById_shouldReturnOkWithOrderInApiResponse_whenOrderExists() throws Exception {
        Long orderId = 20L;
        Order foundOrder = new Order(orderId, "Specific Item", 5);

        when(mockedOrderService.getOrderById(orderId)).thenReturn(Optional.of(foundOrder));
        mockLocalizedMessage("order.fetched.successfully", "Specific order fetched successfully message");

        mockMvc.perform(get("/api/v1/orders/{orderId}", orderId).contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is("SUCCESS")))
                .andExpect(jsonPath("$.statusCode", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.message", is("Specific order fetched successfully message")))
                .andExpect(jsonPath("$.data.orderId", is(orderId.intValue())))
                .andExpect(jsonPath("$.data.item", is("Specific Item")))
                .andExpect(jsonPath("$.data.quantity", is(5)));

        verify(mockedOrderService, times(1)).getOrderById(orderId);
        verify(mockedMessageSource, times(1)).getMessage(eq("order.fetched.successfully"), any(), any(Locale.class));
    }

    @Test
    void getOrderById_shouldReturnNotFoundWithApiResponse_whenOrderDoesNotExist() throws Exception {
        Long orderId = 21L;

        when(mockedOrderService.getOrderById(orderId)).thenReturn(Optional.empty());
        mockLocalizedMessage("order.not.found", "Order not found message");

        mockMvc.perform(get("/api/v1/orders/{orderId}", orderId).contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is("FAILURE")))
                .andExpect(jsonPath("$.statusCode", is(HttpStatus.NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is("Order not found message")))
                .andExpect(jsonPath("$.data", nullValue()));
        verify(mockedOrderService, times(1)).getOrderById(orderId);
        verify(mockedMessageSource, times(1)).getMessage(eq("order.not.found"), any(), any(Locale.class));
    }
}
