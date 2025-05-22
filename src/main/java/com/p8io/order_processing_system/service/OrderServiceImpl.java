package com.p8io.order_processing_system.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.p8io.order_processing_system.dto.OrderRequestDTO;
import com.p8io.order_processing_system.entity.Order;
import com.p8io.order_processing_system.exceptions.OrderAlreadyExistsException;
import com.p8io.order_processing_system.exceptions.OrderNotFoundException;
import com.p8io.order_processing_system.messaging.KafkaProducer;
import com.p8io.order_processing_system.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
public class OrderServiceImpl implements OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
    private final OrderRepository orderRepository;
    private final MessageSource messageSource;
    private final KafkaProducer kafkaProducer;

    private String getLocalizedMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, Locale.ENGLISH);
    }

    @Value("${app.kafka.order-topic}")
    protected String orderPushTopic;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, MessageSource messageSource, KafkaProducer kafkaProducer) {
        this.orderRepository = orderRepository;
        this.messageSource = messageSource;
        this.kafkaProducer = kafkaProducer;
    }


    @Override
    @Transactional
    public Order createOrder(OrderRequestDTO orderRequestDTO) {
        log.info("Creating Order for Order id :: {}", orderRequestDTO.getOrderId());

        if (orderRepository.existsById(orderRequestDTO.getOrderId())) {
            throw new OrderAlreadyExistsException(getLocalizedMessage("order.already.exists", orderRequestDTO.getOrderId()));
        }

        Order order = new Order();
        order.setOrderId(orderRequestDTO.getOrderId());
        order.setItem(orderRequestDTO.getItem());
        order.setQuantity(orderRequestDTO.getQuantity());
        Order savedOrder = orderRepository.saveAndFlush(order);
        log.info("order saved to Db with orderId ::{}", savedOrder.getOrderId());


        String orderJsonString;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            orderJsonString = objectMapper.writeValueAsString(savedOrder);
            log.debug("Serialized saved order [ID: {}] for Kafka", savedOrder.getOrderId());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize saved order [ID: {}] to JSON for Kafka", savedOrder.getOrderId(), e);
            throw new RuntimeException("Failed to serialize order for Kafka", e);
        }
        try {
            log.info("Delegating generic Kafka send for order [ID: {}]", savedOrder.getOrderId());
            kafkaProducer.send(orderPushTopic, savedOrder.getOrderId().toString(), orderJsonString);
            log.info("Generic Kafka send attempt completed for order [ID: {}].", savedOrder.getOrderId());
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Failed to send order to Kafka synchronously",e);
        }
        return savedOrder;
    }

    @Override
    public List<Order> getAllOrders() {
        log.info("Call to fetch all orders");
        List<Order> orderList = orderRepository.findAll();
        log.info("orders Size: {}", orderList.size());
        return orderList;
    }

    @Override
    public Optional<Order> getOrderById(Long orderId) {
        log.info("fetching order by id: {}", orderId);
        return orderRepository.findById(orderId);
    }

    @Override
    @Transactional
    public Order updateOrder(Long orderId, OrderRequestDTO orderRequestDTO) {
        log.info("update order call for orderId: {}", orderId);
        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.warn("order with id {} not found for update ", orderId);
                    return new OrderNotFoundException("Order Not Found for "+ orderId);
                });
        existingOrder.setItem(orderRequestDTO.getItem());
        existingOrder.setQuantity(orderRequestDTO.getQuantity());
        return existingOrder;
    }

    @Override
    @Transactional
    public void deleteOrder(Long orderId) {
        log.info("Deleting order with id: {}", orderId);
        if (!orderRepository.existsById(orderId)){
            log.warn("Order with id {} not found for deleting.", orderId);
            throw  new OrderNotFoundException("order not found with id:"+orderId);
        }
        orderRepository.deleteById(orderId);
    }
}
