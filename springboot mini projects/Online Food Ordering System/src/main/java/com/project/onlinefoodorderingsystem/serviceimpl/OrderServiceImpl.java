package com.project.onlinefoodorderingsystem.serviceimpl;

import com.project.onlinefoodorderingsystem.dto.OrderPatchRequest;
import com.project.onlinefoodorderingsystem.dto.OrderRequest;
import com.project.onlinefoodorderingsystem.exception.OrderNotFoundException;
import com.project.onlinefoodorderingsystem.model.Order;
import com.project.onlinefoodorderingsystem.service.OrderService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class OrderServiceImpl implements OrderService {

    private final ConcurrentMap<Long, Order> orders =
            new ConcurrentHashMap<>();

    // Generate unique order IDs
    private final AtomicLong idGenerator =
            new AtomicLong(1000);

    // Get all orders
    @Override
    public List<Order> getAllOrders() {

        List<Order> orderList =
                new ArrayList<>(orders.values());

        if (orderList.isEmpty()) {
            throw new OrderNotFoundException("Orders Not Found");
        }

        orderList.sort(
                Comparator.comparing(Order::getOrderId)
        );

        return orderList;
    }

    // Get order by ID
    @Override
    public Order getOrderById(Long id) {

        Order order = orders.get(id);

        if (order == null) {
            throw new OrderNotFoundException("Order not found with ID: " + id);
        }

        return order;
    }

    // Create new order
    @Override
    public Order createOrder(OrderRequest request) {

        Long id = idGenerator.incrementAndGet();

        Order order = Order.builder().orderId(id).customerName(request.getCustomerName())
                .foodItem(request.getFoodItem()).quantity(request.getQuantity())
                .price(request.getPrice()).build();

        orders.put(id, order);

        return order;
    }

    // Update complete order
    @Override
    public Order updateOrder(
            Long id, OrderRequest request) {

        if (!orders.containsKey(id)) {
            throw new OrderNotFoundException("Order not found with ID: " + id);
        }

        Order updatedOrder = Order.builder().orderId(id).customerName(request.getCustomerName())
                .foodItem(request.getFoodItem()).quantity(request.getQuantity())
                .price(request.getPrice()).build();

        orders.put(id, updatedOrder);

        return updatedOrder;
    }

    // Partially update order
    @Override
    public Order patchOrder(
            Long id, OrderPatchRequest request) {

        Order order = getOrderById(id);

        if(order == null) {
            throw new OrderNotFoundException("Order not found with ID: " + id);
        }

        if (request.getCustomerName() != null) {

            if (request.getCustomerName().isBlank()) {
                throw new IllegalArgumentException(
                        "Customer name cannot be empty"
                );
            }

            order.setCustomerName(
                    request.getCustomerName()
            );
        }

        if (request.getFoodItem() != null) {

            if (request.getFoodItem().isBlank()) {
                throw new IllegalArgumentException(
                        "Food item cannot be empty"
                );
            }

            order.setFoodItem(request.getFoodItem());
        }

        if (request.getQuantity() != null) {
            order.setQuantity(request.getQuantity());
        }

        if (request.getPrice() != null) {
            order.setPrice(request.getPrice());
        }

        orders.put(id, order);

        return order;
    }

    // Delete order
    @Override
    public void deleteOrder(Long id) {

        Order removedOrder = orders.remove(id);

        if (removedOrder == null) {
            throw new OrderNotFoundException("Order not found with ID: " + id);
        }
    }
}
