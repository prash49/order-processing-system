package com.p8io.order_processing_system.service;

import com.p8io.order_processing_system.dto.OrderRequestDTO;
import com.p8io.order_processing_system.entity.Order;
import com.p8io.order_processing_system.exceptions.OrderAlreadyExistsException;
import com.p8io.order_processing_system.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.List;
import java.util.Optional;

public class OrderServiceImpl implements OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
    private final OrderRepository orderRepository;
    private final MessageSource messageSource;

    private String getLocalizedMessage(String code,Object... args){
        return messageSource.getMessage(code,args, LocaleContextHolder.getLocale());
    }
    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, MessageSource messageSource) {
        this.orderRepository = orderRepository;
        this.messageSource = messageSource;
    }


    @Override
    @Transactional
    public Order createOrder(OrderRequestDTO orderRequestDTO) {
        log.info("Creating Order for Order id :: {}", orderRequestDTO.getOrderId());

        if (orderRepository.existsById(orderRequestDTO.getOrderId())){
            throw  new OrderAlreadyExistsException(getLocalizedMessage("order.already.exists"));
        }

        Order order = new Order();
        order.setOrderId(orderRequestDTO.getOrderId());
        order.setItem(orderRequestDTO.getItem());
        order.setQuantity(orderRequestDTO.getQuantity());
        Order savedOrder = orderRepository.save(order);
        log.info("order saved to Db with orderId ::{}",savedOrder.getOrderId());
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
}
