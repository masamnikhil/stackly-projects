package com.project.movieticketbooking.controller;

import com.project.movieticketbooking.dto.TicketRequest;
import com.project.movieticketbooking.model.Ticket;
import com.project.movieticketbooking.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    // Book movie ticket
    @PostMapping
    public ResponseEntity<Ticket> bookTicket(
            @Valid @RequestBody TicketRequest dto) {

        return ResponseEntity.ok(
                ticketService.bookTicket(dto)
        );
    }

    // View all tickets
    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {

        return ResponseEntity.ok(
                ticketService.getAllTickets()
        );
    }

    // Find ticket by ID
    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicketById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ticketService.getTicketById(id)
        );
    }

    // Delete ticket
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTicket(
            @PathVariable Long id) {

        ticketService.cancelTicket(id);

        return ResponseEntity.ok(
                "Movie ticket deleted successfully"
        );
    }
}
