package com.anahuergo.helpdesk.controller;

import com.anahuergo.helpdesk.domain.Ticket;
import com.anahuergo.helpdesk.domain.User;
import com.anahuergo.helpdesk.repository.TicketRepository;
import com.anahuergo.helpdesk.repository.UserRepository;
import com.anahuergo.helpdesk.domain.TicketStatus;
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

    @PutMapping("/{id}/status")
    public Ticket updateStatus(@PathVariable Long id, @RequestParam String status) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow();
        ticket.setStatus(TicketStatus.valueOf(status));
        
        if (status.equals("RESOLVED")) {
            ticket.setResolvedAt(java.time.LocalDateTime.now());
        }
        if (status.equals("CLOSED")) {
            ticket.setClosedAt(java.time.LocalDateTime.now());
        }
        
        return ticketRepository.save(ticket);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        ticketRepository.deleteById(id);
    }

    @PutMapping("/{id}/assign")
    public Ticket assign(@PathVariable Long id, @RequestParam Long agentId) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow();
        User agent = userRepository.findById(agentId).orElseThrow();
        ticket.setAssignee(agent);
        ticket.setStatus(TicketStatus.OPEN);
        return ticketRepository.save(ticket);
    }

}