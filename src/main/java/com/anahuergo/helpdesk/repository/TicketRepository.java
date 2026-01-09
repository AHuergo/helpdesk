package com.anahuergo.helpdesk.repository;

import com.anahuergo.helpdesk.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByCompanyId(Long companyId);

    Optional<Ticket> findByIdAndCompanyId(Long id, Long companyId);

    List<Ticket> findByStatusAndCompanyId(TicketStatus status, Long companyId);

    List<Ticket> findByPriorityAndCompanyId(TicketPriority priority, Long companyId);

    List<Ticket> findByAssigneeAndCompanyId(User assignee, Long companyId);

    List<Ticket> findByRequesterAndCompanyId(User requester, Long companyId);

    List<Ticket> findByQueueAndCompanyId(Queue queue, Long companyId);

    List<Ticket> findByFirstResponseDueAtBeforeAndStatusNotAndCompanyId(LocalDateTime date, TicketStatus status, Long companyId);

    List<Ticket> findByStatus(TicketStatus status);
    List<Ticket> findByPriority(TicketPriority priority);
    List<Ticket> findByAssignee(User assignee);
    List<Ticket> findByRequester(User requester);
    List<Ticket> findByQueue(Queue queue);
    List<Ticket> findByFirstResponseDueAtBeforeAndStatusNot(LocalDateTime date, TicketStatus status);

}