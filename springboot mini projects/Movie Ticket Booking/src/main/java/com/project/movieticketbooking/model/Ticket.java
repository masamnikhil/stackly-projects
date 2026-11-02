package com.project.movieticketbooking.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Ticket {

    private Long ticketId;
    private String movieName;
    private String customerName;
    private String seatNumber;
    private BigDecimal ticketPrice;
}
