package com.anahuergo.helpdesk.repository;

import com.anahuergo.helpdesk.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByTenantId(Long tenantId);

    Optional<Ticket> findByIdAndTenantId(Long id, Long tenantId);

    List<Ticket> findByStatusAndTenantId(TicketStatus status, Long tenantId);

    List<Ticket> findByPriorityAndTenantId(TicketPriority priority, Long tenantId);

    List<Ticket> findByAssigneeAndTenantId(User assignee, Long tenantId);

    List<Ticket> findByRequesterAndTenantId(User requester, Long tenantId);

    List<Ticket> findByQueueAndTenantId(Queue queue, Long tenantId);

    List<Ticket> findByFirstResponseDueAtBeforeAndStatusNotAndTenantId(LocalDateTime date, TicketStatus status, Long tenantId);

    List<Ticket> findByStatus(TicketStatus status);
    List<Ticket> findByPriority(TicketPriority priority);
    List<Ticket> findByAssignee(User assignee);
    List<Ticket> findByRequester(User requester);
    List<Ticket> findByQueue(Queue queue);
    List<Ticket> findByFirstResponseDueAtBeforeAndStatusNot(LocalDateTime date, TicketStatus status);

}