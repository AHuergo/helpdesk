package com.anahuergo.helpdesk.controller;

import com.anahuergo.helpdesk.domain.*;
import com.anahuergo.helpdesk.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class TicketMessageControllerTest {

    @Autowired
    private TicketMessageController messageController;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    private User requester;
    private User agent;
    private Ticket ticket;

    @BeforeEach
    void setUp() {
        requester = new User();
        requester.setEmail("requester@test.com");
        requester.setPassword("12345678");
        requester.setName("Requester");
        requester.setSurname("Test");
        requester = userRepository.save(requester);

        agent = new User();
        agent.setEmail("agent@test.com");
        agent.setPassword("12345678");
        agent.setName("Agent");
        agent.setSurname("Test");
        agent.setRole(UserRole.AGENT);
        agent = userRepository.save(agent);

        ticket = new Ticket();
        ticket.setSubject("Ticket con mensajes");
        ticket.setDescription("Test");
        ticket.setCode("TCK-TEST-001");
        ticket.setRequester(requester);
        ticket = ticketRepository.save(ticket);
    }

    @Test
    void shouldCreatePublicMessage() {
        var message = messageController.create(
            ticket.getId(),
            requester.getId(),
            "PUBLIC",
            "Tengo un problema con mi cuenta"
        );

        assertNotNull(message);
        assertEquals("Tengo un problema con mi cuenta", message.getBody());
        assertEquals(MessageVisibility.PUBLIC, message.getVisibility());
    }

    @Test
    void shouldCreateInternalNote() {
        var message = messageController.create(
            ticket.getId(),
            agent.getId(),
            "INTERNAL",
            "Revisar historial del cliente"
        );

        assertEquals(MessageVisibility.INTERNAL, message.getVisibility());
    }

    @Test
    void shouldListMessagesByTicket() {
        messageController.create(ticket.getId(), requester.getId(), "PUBLIC", "Mensaje 1");
        messageController.create(ticket.getId(), agent.getId(), "PUBLIC", "Mensaje 2");
        messageController.create(ticket.getId(), agent.getId(), "INTERNAL", "Nota interna");

        var messages = messageController.findByTicket(ticket.getId());

        assertEquals(3, messages.size());
    }

}