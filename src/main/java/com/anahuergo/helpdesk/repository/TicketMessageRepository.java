package com.anahuergo.helpdesk.repository;

import com.anahuergo.helpdesk.domain.Ticket;
import com.anahuergo.helpdesk.domain.TicketMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TicketMessageRepository extends JpaRepository<TicketMessage, Long> {

    List<TicketMessage> findByTicketOrderByCreatedAtAsc(Ticket ticket);

}
