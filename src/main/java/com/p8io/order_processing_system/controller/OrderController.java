package com.p8io.order_processing_system.controller;

import com.p8io.order_processing_system.dto.ApiResponse;
import com.p8io.order_processing_system.dto.OrderRequestDTO;
import com.p8io.order_processing_system.entity.Order;
import com.p8io.order_processing_system.service.OrderService;
import com.p8io.order_processing_system.util.ApplicationConstants;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    private final MessageSource messageSource;
    private final OrderService orderService;

    @Autowired
    public OrderController(MessageSource messageSource, OrderService orderService) {
        this.messageSource = messageSource;
        this.orderService = orderService;
    }

    private String getLocalizedMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Order>> createOrder(@Valid @RequestBody OrderRequestDTO orderRequestDTO) {
        log.info("request payload :: {}", orderRequestDTO);
        Order order = orderService.createOrder(orderRequestDTO);
        ApiResponse<Order> response = new ApiResponse<>(ApplicationConstants.SUCCESS, HttpStatus.OK.value(), getLocalizedMessage("order.created.successfully", orderRequestDTO.getOrderId()),order);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Order>>> getAllOrders(){
        log.info("call to fetch All orders ");
        List<Order> orders = orderService.getAllOrders();
        ApiResponse<List<Order>> response = new ApiResponse<>(ApplicationConstants.SUCCESS, HttpStatus.OK.value(), getLocalizedMessage("orders.fetched.successfully"), orders);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/{orderId}")
    public ResponseEntity<ApiResponse<Order>> getOrderById(@PathVariable Long orderId){
        log.info("order Id to fetch Order :: {}",orderId);
        Optional<Order> order = orderService.getOrderById(orderId);
        if(order.isPresent()){
            ApiResponse<Order> response = new ApiResponse<>(ApplicationConstants.SUCCESS,HttpStatus.OK.value(), getLocalizedMessage("order.fetched.successfully", orderId), order.get());
            return ResponseEntity.ok(response);
        }else {
            ApiResponse<Order> response = new ApiResponse<>(ApplicationConstants.FAILURE, HttpStatus.NOT_FOUND.value(), getLocalizedMessage("order.not.found", orderId));
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}
