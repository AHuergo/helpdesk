package com.anahuergo.helpdesk.controller;

import com.anahuergo.helpdesk.domain.*;
import com.anahuergo.helpdesk.dto.TicketEventResponse;
import com.anahuergo.helpdesk.dto.TicketResponse;
import com.anahuergo.helpdesk.repository.*;
import com.anahuergo.helpdesk.security.CurrentUserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final CurrentUserService currentUserService;

    public TicketController(TicketRepository ticketRepository, 
                            UserRepository userRepository, 
                            QueueRepository queueRepository,
                            SlaPolicyRepository slaPolicyRepository,
                            TicketEventRepository ticketEventRepository,
                            CurrentUserService currentUserService) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.queueRepository = queueRepository;
        this.slaPolicyRepository = slaPolicyRepository;
        this.ticketEventRepository = ticketEventRepository;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public List<TicketResponse> findAll() {
        Long companyId = currentUserService.getCurrentCompanyId();
        if (companyId == null) {
            return ticketRepository.findAll().stream()
                    .map(TicketResponse::new)
                    .toList();
        }
        return ticketRepository.findByCompanyId(companyId).stream()
                .map(TicketResponse::new)
                .toList();
    }

    @GetMapping("/{id}")
    public TicketResponse findById(@PathVariable Long id) {
        Long companyId = currentUserService.getCurrentCompanyId();
        Ticket ticket;
        if (companyId == null) {
            ticket = ticketRepository.findById(id).orElseThrow();
        } else {
            ticket = ticketRepository.findByIdAndCompanyId(id, companyId).orElseThrow();
        }
        return new TicketResponse(ticket);
    }

    @PostMapping
    public TicketResponse create(@Valid @RequestBody Ticket ticket, 
                                 @RequestParam(required = false) Long requesterId,
                                 @RequestParam(required = false) Long slaPolicyId) {
        User currentUser = currentUserService.getCurrentUser();
        
        User requester;
        if (requesterId != null) {
            requester = userRepository.findById(requesterId).orElseThrow();
        } else {
            requester = currentUser;
        }
        
        ticket.setRequester(requester);
        ticket.setCompany(currentUser.getCompany());
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
        Long companyId = currentUserService.getCurrentCompanyId();
        Ticket ticket;
        if (companyId == null) {
            ticket = ticketRepository.findById(id).orElseThrow();
        } else {
            ticket = ticketRepository.findByIdAndCompanyId(id, companyId).orElseThrow();
        }
        
        String oldStatus = ticket.getStatus().name();
        
        ticket.setStatus(TicketStatus.valueOf(status));
        
        if (status.equals("RESOLVED")) {
            ticket.setResolvedAt(java.time.LocalDateTime.now());
        }
        if (status.equals("CLOSED")) {
            ticket.setClosedAt(java.time.LocalDateTime.now());
        }
        
        Ticket saved = ticketRepository.save(ticket);
        
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
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public TicketResponse assign(@PathVariable Long id, @RequestParam Long agentId) {
        Long companyId = currentUserService.getCurrentCompanyId();
        Ticket ticket;
        if (companyId == null) {
            ticket = ticketRepository.findById(id).orElseThrow();
        } else {
            ticket = ticketRepository.findByIdAndCompanyId(id, companyId).orElseThrow();
        }
        
        String oldAssignee = ticket.getAssignee() != null ? ticket.getAssignee().getName() : null;
        
        User agent = userRepository.findById(agentId).orElseThrow();
        ticket.setAssignee(agent);
        ticket.setStatus(TicketStatus.OPEN);
        Ticket saved = ticketRepository.save(ticket);
        
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
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public TicketResponse assignQueue(@PathVariable Long id, @RequestParam Long queueId) {
        Long companyId = currentUserService.getCurrentCompanyId();
        Ticket ticket;
        if (companyId == null) {
            ticket = ticketRepository.findById(id).orElseThrow();
        } else {
            ticket = ticketRepository.findByIdAndCompanyId(id, companyId).orElseThrow();
        }
        
        Queue queue = queueRepository.findById(queueId).orElseThrow();
        ticket.setQueue(queue);
        Ticket saved = ticketRepository.save(ticket);
        return new TicketResponse(saved);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        Long companyId = currentUserService.getCurrentCompanyId();
        if (companyId != null) {
            ticketRepository.findByIdAndCompanyId(id, companyId).orElseThrow();
        }
        ticketRepository.deleteById(id);
    }

    @GetMapping("/status/{status}")
    public List<TicketResponse> findByStatus(@PathVariable String status) {
        Long companyId = currentUserService.getCurrentCompanyId();
        if (companyId == null) {
            return ticketRepository.findByStatus(TicketStatus.valueOf(status)).stream()
                    .map(TicketResponse::new)
                    .toList();
        }
        return ticketRepository.findByStatusAndCompanyId(TicketStatus.valueOf(status), companyId).stream()
                .map(TicketResponse::new)
                .toList();
    }

    @GetMapping("/priority/{priority}")
    public List<TicketResponse> findByPriority(@PathVariable String priority) {
        Long companyId = currentUserService.getCurrentCompanyId();
        if (companyId == null) {
            return ticketRepository.findByPriority(TicketPriority.valueOf(priority)).stream()
                    .map(TicketResponse::new)
                    .toList();
        }
        return ticketRepository.findByPriorityAndCompanyId(TicketPriority.valueOf(priority), companyId).stream()
                .map(TicketResponse::new)
                .toList();
    }

    @GetMapping("/assignee/{agentId}")
    public List<TicketResponse> findByAssignee(@PathVariable Long agentId) {
        User agent = userRepository.findById(agentId).orElseThrow();
        Long companyId = currentUserService.getCurrentCompanyId();
        if (companyId == null) {
            return ticketRepository.findByAssignee(agent).stream()
                    .map(TicketResponse::new)
                    .toList();
        }
        return ticketRepository.findByAssigneeAndCompanyId(agent, companyId).stream()
                .map(TicketResponse::new)
                .toList();
    }

    @GetMapping("/queue/{queueId}")
    public List<TicketResponse> findByQueue(@PathVariable Long queueId) {
        Queue queue = queueRepository.findById(queueId).orElseThrow();
        Long companyId = currentUserService.getCurrentCompanyId();
        if (companyId == null) {
            return ticketRepository.findByQueue(queue).stream()
                    .map(TicketResponse::new)
                    .toList();
        }
        return ticketRepository.findByQueueAndCompanyId(queue, companyId).stream()
                .map(TicketResponse::new)
                .toList();
    }

    @GetMapping("/overdue")
    public List<TicketResponse> findOverdue() {
        Long companyId = currentUserService.getCurrentCompanyId();
        if (companyId == null) {
            return ticketRepository.findByFirstResponseDueAtBeforeAndStatusNot(
                    java.time.LocalDateTime.now(), 
                    TicketStatus.CLOSED
            ).stream()
                    .map(TicketResponse::new)
                    .toList();
        }
        return ticketRepository.findByFirstResponseDueAtBeforeAndStatusNotAndCompanyId(
                java.time.LocalDateTime.now(), 
                TicketStatus.CLOSED,
                companyId
        ).stream()
                .map(TicketResponse::new)
                .toList();
    }

    @GetMapping("/{id}/events")
    public List<TicketEventResponse> getEvents(@PathVariable Long id) {
        Long companyId = currentUserService.getCurrentCompanyId();
        Ticket ticket;
        if (companyId == null) {
            ticket = ticketRepository.findById(id).orElseThrow();
        } else {
            ticket = ticketRepository.findByIdAndCompanyId(id, companyId).orElseThrow();
        }
        return ticketEventRepository.findByTicketOrderByCreatedAtAsc(ticket).stream()
                .map(TicketEventResponse::new)
                .toList();
    }

}