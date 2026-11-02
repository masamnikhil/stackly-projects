package com.project.onlinefoodorderingsystem.service;

import com.project.onlinefoodorderingsystem.dto.OrderPatchRequest;
import com.project.onlinefoodorderingsystem.dto.OrderRequest;
import com.project.onlinefoodorderingsystem.model.Order;

import java.util.List;

public interface OrderService {

    List<Order> getAllOrders();

    Order getOrderById(Long id);

    Order createOrder(OrderRequest request);

    Order updateOrder(Long id, OrderRequest request);

    Order patchOrder(Long id, OrderPatchRequest request);

    void deleteOrder(Long id);
}
