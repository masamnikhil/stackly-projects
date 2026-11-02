package com.project.onlinefoodorderingsystem.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Order {

    private Long orderId;
    private String customerName;
    private String foodItem;
    private Integer quantity;
    private BigDecimal price;


}
