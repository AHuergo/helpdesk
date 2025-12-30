package com.anahuergo.helpdesk.controller;

import com.anahuergo.helpdesk.domain.*;
import com.anahuergo.helpdesk.repository.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tickets/{ticketId}/messages")
public class TicketMessageController {

    private final TicketMessageRepository messageRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public TicketMessageController(TicketMessageRepository messageRepository, TicketRepository ticketRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<TicketMessage> findByTicket(@PathVariable Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow();
        return messageRepository.findByTicketOrderByCreatedAtAsc(ticket);
    }

    @PostMapping
    public TicketMessage create(@PathVariable Long ticketId, @RequestParam Long authorId, @RequestParam(defaultValue = "PUBLIC") String visibility, @RequestBody String body) {
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow();
        User author = userRepository.findById(authorId).orElseThrow();

        TicketMessage message = new TicketMessage();
        message.setTicket(ticket);
        message.setAuthor(author);
        message.setBody(body);
        message.setVisibility(MessageVisibility.valueOf(visibility));

        return messageRepository.save(message);
    }

}