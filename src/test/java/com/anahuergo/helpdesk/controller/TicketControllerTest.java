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
public class TicketControllerTest {

    @Autowired
    private TicketController ticketController;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SlaPolicyRepository slaPolicyRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("test@test.com");
        testUser.setPassword("12345678");
        testUser.setName("Test");
        testUser.setSurname("User");
        testUser = userRepository.save(testUser);
    }

    @Test
    void shouldCreateTicket() {
        Ticket ticket = new Ticket();
        ticket.setSubject("Test ticket");
        ticket.setDescription("Test description");

        var response = ticketController.create(ticket, testUser.getId(), null);

        assertNotNull(response);
        assertEquals("Test ticket", response.getSubject());
        assertEquals("NEW", response.getStatus().name());
    }

    @Test
    void shouldFindTicketById() {
        Ticket ticket = new Ticket();
        ticket.setSubject("Find me");
        ticket.setDescription("Test");

        var created = ticketController.create(ticket, testUser.getId(), null);
        var found = ticketController.findById(created.getId());

        assertNotNull(found);
        assertEquals("Find me", found.getSubject());
    }

    @Test
    void shouldUpdateStatus() {
        Ticket ticket = new Ticket();
        ticket.setSubject("Status test");
        ticket.setDescription("Test");

        var created = ticketController.create(ticket, testUser.getId(), null);
        var updated = ticketController.updateStatus(created.getId(), "RESOLVED", null);

        assertEquals("RESOLVED", updated.getStatus().name());
        assertNotNull(updated.getResolvedAt());
    }

    @Test
    void shouldAssignAgent() {
        // Crear agente
        User agent = new User();
        agent.setEmail("agent@test.com");
        agent.setPassword("12345678");
        agent.setName("Agent");
        agent.setSurname("Test");
        agent.setRole(UserRole.AGENT);
        agent = userRepository.save(agent);

        // Crear ticket
        Ticket ticket = new Ticket();
        ticket.setSubject("Assign test");
        ticket.setDescription("Test");

        var created = ticketController.create(ticket, testUser.getId(), null);
        var assigned = ticketController.assign(created.getId(), agent.getId());

        assertEquals("Agent", assigned.getAssignee().getName());
        assertEquals("OPEN", assigned.getStatus().name());
    }

    @Test
    void shouldFindByStatus() {
        Ticket ticket = new Ticket();
        ticket.setSubject("Filter test");
        ticket.setDescription("Test");

        ticketController.create(ticket, testUser.getId(), null);
        var results = ticketController.findByStatus("NEW");

        assertFalse(results.isEmpty());
    }

    @Test
    void shouldRecordStatusChangeEvent() {
        Ticket ticket = new Ticket();
        ticket.setSubject("Event test");
        ticket.setDescription("Test");

        var created = ticketController.create(ticket, testUser.getId(), null);
        ticketController.updateStatus(created.getId(), "OPEN", testUser.getId());

        var events = ticketController.getEvents(created.getId());

        assertFalse(events.isEmpty());
        assertEquals("STATUS_CHANGED", events.get(0).getEventType());
        assertEquals("NEW", events.get(0).getOldValue());
        assertEquals("OPEN", events.get(0).getNewValue());
    }

    @Test
    void shouldRecordAssignmentEvent() {
        User agent = new User();
        agent.setEmail("agent2@test.com");
        agent.setPassword("12345678");
        agent.setName("Agent2");
        agent.setSurname("Test");
        agent.setRole(UserRole.AGENT);
        agent = userRepository.save(agent);

        Ticket ticket = new Ticket();
        ticket.setSubject("Assignment event test");
        ticket.setDescription("Test");

        var created = ticketController.create(ticket, testUser.getId(), null);
        ticketController.assign(created.getId(), agent.getId());

        var events = ticketController.getEvents(created.getId());

        assertFalse(events.isEmpty());
        assertEquals("ASSIGNED", events.get(0).getEventType());
        assertEquals("Agent2", events.get(0).getNewValue());
    }

    @Test
    void shouldCalculateSlaDeadlines() {
        SlaPolicy policy = new SlaPolicy();
        policy.setName("Test SLA");
        policy.setFirstResponseMinutes(60);
        policy.setResolutionMinutes(480);
        policy = slaPolicyRepository.save(policy);

        Ticket ticket = new Ticket();
        ticket.setSubject("SLA test");
        ticket.setDescription("Test");

        var created = ticketController.create(ticket, testUser.getId(), policy.getId());

        assertNotNull(created.getFirstResponseDueAt());
        assertNotNull(created.getResolutionDueAt());
        assertEquals("Test SLA", created.getSlaPolicyName());
    }

    @Test
    void shouldNotHaveSlaWithoutPolicy() {
        Ticket ticket = new Ticket();
        ticket.setSubject("No SLA test");
        ticket.setDescription("Test");

        var created = ticketController.create(ticket, testUser.getId(), null);

        assertNull(created.getFirstResponseDueAt());
        assertNull(created.getResolutionDueAt());
        assertNull(created.getSlaPolicyName());
    }

}