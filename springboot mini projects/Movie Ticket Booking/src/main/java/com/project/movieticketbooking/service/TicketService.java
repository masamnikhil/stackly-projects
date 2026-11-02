package com.project.movieticketbooking.service;

import com.project.movieticketbooking.dto.TicketRequest;
import com.project.movieticketbooking.model.Ticket;

import java.util.List;

public interface TicketService {

    Ticket bookTicket(TicketRequest dto);
    List<Ticket> getAllTickets();
    Ticket getTicketById(Long id);
    void cancelTicket(Long id);

}
