package com.anahuergo.helpdesk.controller;

import com.anahuergo.helpdesk.domain.Ticket;
import com.anahuergo.helpdesk.domain.TicketPriority;
import com.anahuergo.helpdesk.domain.User;
import com.anahuergo.helpdesk.repository.TicketRepository;
import com.anahuergo.helpdesk.repository.UserRepository;
import com.anahuergo.helpdesk.domain.TicketStatus;
import com.anahuergo.helpdesk.domain.Queue;
import com.anahuergo.helpdesk.domain.SlaPolicy;
import com.anahuergo.helpdesk.repository.QueueRepository;
import com.anahuergo.helpdesk.repository.SlaPolicyRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final QueueRepository queueRepository;
    private final SlaPolicyRepository slaPolicyRepository;

    public TicketController(TicketRepository ticketRepository, UserRepository userRepository, QueueRepository queueRepository, SlaPolicyRepository slaPolicyRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.queueRepository = queueRepository;
        this.slaPolicyRepository = slaPolicyRepository;
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
    public Ticket create(@RequestBody Ticket ticket, @RequestParam Long requesterId, @RequestParam(required=false) Long slaPolicyId) {
        User requester = userRepository.findById(requesterId).orElseThrow();
        ticket.setRequester(requester);
        ticket.setCode("TCK-" + System.currentTimeMillis());

        if (slaPolicyId != null) {
            SlaPolicy policy = slaPolicyRepository.findById(slaPolicyId).orElseThrow();
            ticket.setSlaPolicy(policy);
            
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            ticket.setFirstResponseDueAt(now.plusMinutes(policy.getFirstResponseMinutes()));
            ticket.setResolutionDueAt(now.plusMinutes(policy.getResolutionMinutes()));
        }

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

    @GetMapping("/status/{status}")
    public List<Ticket> findByStatus(@PathVariable String status) {
        return ticketRepository.findByStatus(TicketStatus.valueOf(status));
    }

    @GetMapping("/priority/{priority}")
    public List<Ticket> findByPriority(@PathVariable String priority) {
        return ticketRepository.findByPriority(TicketPriority.valueOf(priority));
    }

    @GetMapping("/assignee/{agentId}")
    public List<Ticket> findByAssignee(@PathVariable Long agentId) {
        User agent = userRepository.findById(agentId).orElseThrow();
        return ticketRepository.findByAssignee(agent);
    }

    @PutMapping("/{id}/queue")
    public Ticket assignQueue(@PathVariable Long id, @RequestParam Long queueId) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow();
        Queue queue = queueRepository.findById(queueId).orElseThrow();
        ticket.setQueue(queue);
        return ticketRepository.save(ticket);
    }

    @GetMapping("/queue/{queueId}")
    public List<Ticket> findByQueue(@PathVariable Long queueId) {
        Queue queue = queueRepository.findById(queueId).orElseThrow();
        return ticketRepository.findByQueue(queue);
    }

    @GetMapping("/overdue")
    public List<Ticket> findOverdue() {
        return ticketRepository.findByFirstResponseDueAtBeforeAndStatusNot(
            java.time.LocalDateTime.now(), 
            TicketStatus.CLOSED
        );
    }
}