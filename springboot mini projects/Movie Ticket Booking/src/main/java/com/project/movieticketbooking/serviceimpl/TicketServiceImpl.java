package com.project.movieticketbooking.serviceimpl;

import com.project.movieticketbooking.dto.TicketRequest;
import com.project.movieticketbooking.exception.TicketNotFoundException;
import com.project.movieticketbooking.model.Ticket;
import com.project.movieticketbooking.service.TicketService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TicketServiceImpl implements TicketService {

    private final Map<Long, Ticket> tickets =
            new ConcurrentHashMap<>();

    private final AtomicLong idGenerator =
            new AtomicLong(1);

    // Book movie ticket
    public Ticket bookTicket(
            TicketRequest dto) {

        Long id = idGenerator.getAndIncrement();

        Ticket ticket = Ticket.builder().ticketId(id).customerName(dto.getCustomerName())
                .movieName(dto.getMovieName()).seatNumber(dto.getSeatNumber()).ticketPrice(dto.getTicketPrice()).build();

        tickets.put(ticket.getTicketId(), ticket);

        return ticket;
    }

    // View all tickets
    public List<Ticket> getAllTickets() {

        List<Ticket> ticketList = new ArrayList<>(tickets.values());

        if(ticketList.isEmpty()){
            throw new TicketNotFoundException("Tickets Not Found");
        }

        return ticketList;
    }

    // Find ticket by ID
    public Ticket getTicketById(Long id) {

        Ticket ticket = tickets.get(id);

        if (ticket == null) {
            throw new TicketNotFoundException(
                    "Ticket not found with ID: " + id
            );
        }

        return ticket;
    }

    // Delete ticket
    public void cancelTicket(Long id) {

        if (tickets.remove(id) == null) {
            throw new TicketNotFoundException(
                    "Ticket not found with ID: " + id
            );
        }
    }
}
