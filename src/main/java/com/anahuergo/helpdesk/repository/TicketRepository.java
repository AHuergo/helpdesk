package com.anahuergo.helpdesk.repository;

import com.anahuergo.helpdesk.domain.Ticket;
import com.anahuergo.helpdesk.domain.TicketPriority;
import com.anahuergo.helpdesk.domain.TicketStatus;
import com.anahuergo.helpdesk.domain.User;
import com.anahuergo.helpdesk.domain.Queue;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByStatus(TicketStatus status);

    List<Ticket> findByPriority(TicketPriority priority);

    List<Ticket> findByAssignee(User assignee);

    List<Ticket> findByRequester(User requester);

    List<Ticket> findByStatusAndPriority(TicketStatus status, TicketPriority priority);

    List<Ticket> findByQueue(Queue queue);
}