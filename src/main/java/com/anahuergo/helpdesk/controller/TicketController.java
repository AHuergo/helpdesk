package com.anahuergo.helpdesk.controller;

import com.anahuergo.helpdesk.domain.*;
import com.anahuergo.helpdesk.dto.TicketResponse;
import com.anahuergo.helpdesk.repository.*;
import com.anahuergo.helpdesk.dto.TicketEventResponse;

import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final QueueRepository queueRepository;
    private final SlaPolicyRepository slaPolicyRepository;
    private final TicketEventRepository ticketEventRepository;

    public TicketController(TicketRepository ticketRepository, 
                            UserRepository userRepository, 
                            QueueRepository queueRepository,
                            SlaPolicyRepository slaPolicyRepository,
                            TicketEventRepository ticketEventRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.queueRepository = queueRepository;
        this.slaPolicyRepository = slaPolicyRepository;
        this.ticketEventRepository = ticketEventRepository;
    }

    @GetMapping
    public List<TicketResponse> findAll() {
        return ticketRepository.findAll().stream()
                .map(TicketResponse::new)
                .toList();
    }

    @GetMapping("/{id}")
    public TicketResponse findById(@PathVariable Long id) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow();
        return new TicketResponse(ticket);
    }

    @PostMapping
    public TicketResponse create(@RequestBody Ticket ticket, 
                                 @RequestParam Long requesterId,
                                 @RequestParam(required = false) Long slaPolicyId) {
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
        
        Ticket saved = ticketRepository.save(ticket);
        return new TicketResponse(saved);
    }

    @PutMapping("/{id}/status")
    public TicketResponse updateStatus(@PathVariable Long id, 
                                    @RequestParam String status,
                                    @RequestParam(required = false) Long userId) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow();
        String oldStatus = ticket.getStatus().name();
        
        ticket.setStatus(TicketStatus.valueOf(status));
        
        if (status.equals("RESOLVED")) {
            ticket.setResolvedAt(java.time.LocalDateTime.now());
        }
        if (status.equals("CLOSED")) {
            ticket.setClosedAt(java.time.LocalDateTime.now());
        }
        
        Ticket saved = ticketRepository.save(ticket);
        
        // add event
        TicketEvent event = new TicketEvent();
        event.setTicket(saved);
        event.setEventType("STATUS_CHANGED");
        event.setOldValue(oldStatus);
        event.setNewValue(status);
        if (userId != null) {
            event.setUser(userRepository.findById(userId).orElse(null));
        }
        ticketEventRepository.save(event);
        
        return new TicketResponse(saved);
    }

    @PutMapping("/{id}/assign")
    public TicketResponse assign(@PathVariable Long id, @RequestParam Long agentId) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow();
        String oldAssignee = ticket.getAssignee() != null ? ticket.getAssignee().getName() : null;
        
        User agent = userRepository.findById(agentId).orElseThrow();
        ticket.setAssignee(agent);
        ticket.setStatus(TicketStatus.OPEN);
        Ticket saved = ticketRepository.save(ticket);
        
        // event
        TicketEvent event = new TicketEvent();
        event.setTicket(saved);
        event.setEventType("ASSIGNED");
        event.setOldValue(oldAssignee);
        event.setNewValue(agent.getName());
        event.setUser(agent);
        ticketEventRepository.save(event);
        
        return new TicketResponse(saved);
    }

    @PutMapping("/{id}/queue")
    public TicketResponse assignQueue(@PathVariable Long id, @RequestParam Long queueId) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow();
        Queue queue = queueRepository.findById(queueId).orElseThrow();
        ticket.setQueue(queue);
        Ticket saved = ticketRepository.save(ticket);
        return new TicketResponse(saved);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        ticketRepository.deleteById(id);
    }

    @GetMapping("/status/{status}")
    public List<TicketResponse> findByStatus(@PathVariable String status) {
        return ticketRepository.findByStatus(TicketStatus.valueOf(status)).stream()
                .map(TicketResponse::new)
                .toList();
    }

    @GetMapping("/priority/{priority}")
    public List<TicketResponse> findByPriority(@PathVariable String priority) {
        return ticketRepository.findByPriority(TicketPriority.valueOf(priority)).stream()
                .map(TicketResponse::new)
                .toList();
    }

    @GetMapping("/assignee/{agentId}")
    public List<TicketResponse> findByAssignee(@PathVariable Long agentId) {
        User agent = userRepository.findById(agentId).orElseThrow();
        return ticketRepository.findByAssignee(agent).stream()
                .map(TicketResponse::new)
                .toList();
    }

    @GetMapping("/queue/{queueId}")
    public List<TicketResponse> findByQueue(@PathVariable Long queueId) {
        Queue queue = queueRepository.findById(queueId).orElseThrow();
        return ticketRepository.findByQueue(queue).stream()
                .map(TicketResponse::new)
                .toList();
    }

    @GetMapping("/overdue")
    public List<TicketResponse> findOverdue() {
        return ticketRepository.findByFirstResponseDueAtBeforeAndStatusNot(
                java.time.LocalDateTime.now(), 
                TicketStatus.CLOSED
        ).stream()
                .map(TicketResponse::new)
                .toList();
    }

    @GetMapping("/{id}/events")
    public List<TicketEventResponse> getEvents(@PathVariable Long id) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow();
        return ticketEventRepository.findByTicketOrderByCreatedAtAsc(ticket).stream()
                .map(TicketEventResponse::new)
                .toList();
    }

}