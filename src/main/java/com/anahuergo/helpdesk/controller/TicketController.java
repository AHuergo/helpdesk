package com.anahuergo.helpdesk.controller;

import com.anahuergo.helpdesk.domain.Ticket;
import com.anahuergo.helpdesk.domain.User;
import com.anahuergo.helpdesk.repository.TicketRepository;
import com.anahuergo.helpdesk.repository.UserRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public TicketController(TicketRepository ticketRepository, UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Ticket> findAll() {
        return ticketRepository.findAll();
    }

    @GetMapping("/{id}")
    public Ticket findById(@PathVariable Long id) {
        return ticketRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Ticket create(@RequestBody Ticket ticket, @RequestParam Long requesterId) {
        User requester = userRepository.findById(requesterId).orElseThrow();
        ticket.setRequester(requester);
        ticket.setCode("TCK-" + System.currentTimeMillis());
        return ticketRepository.save(ticket);
    }

}