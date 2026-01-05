package com.anahuergo.helpdesk.repository;

import com.anahuergo.helpdesk.domain.Ticket;
import com.anahuergo.helpdesk.domain.TicketEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TicketEventRepository extends JpaRepository<TicketEvent, Long> {

    List<TicketEvent> findByTicketOrderByCreatedAtAsc(Ticket ticket);

}