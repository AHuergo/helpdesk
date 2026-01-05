package com.anahuergo.helpdesk.dto;

import com.anahuergo.helpdesk.domain.*;
import java.time.LocalDateTime;

public class TicketResponse {

    private Long id;
    private String code;
    private String subject;
    private String description;
    private TicketStatus status;
    private TicketPriority priority;
    private TicketChannel channel;
    private UserResponse requester;
    private UserResponse assignee;
    private String queueName;
    private String slaPolicyName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;
    private LocalDateTime closedAt;
    private LocalDateTime firstResponseDueAt;
    private LocalDateTime resolutionDueAt;

    public TicketResponse(Ticket ticket) {
        this.id = ticket.getId();
        this.code = ticket.getCode();
        this.subject = ticket.getSubject();
        this.description = ticket.getDescription();
        this.status = ticket.getStatus();
        this.priority = ticket.getPriority();
        this.channel = ticket.getChannel();
        this.requester = ticket.getRequester() != null ? new UserResponse(ticket.getRequester()) : null;
        this.assignee = ticket.getAssignee() != null ? new UserResponse(ticket.getAssignee()) : null;
        this.queueName = ticket.getQueue() != null ? ticket.getQueue().getName() : null;
        this.slaPolicyName = ticket.getSlaPolicy() != null ? ticket.getSlaPolicy().getName() : null;
        this.createdAt = ticket.getCreatedAt();
        this.updatedAt = ticket.getUpdatedAt();
        this.resolvedAt = ticket.getResolvedAt();
        this.closedAt = ticket.getClosedAt();
        this.firstResponseDueAt = ticket.getFirstResponseDueAt();
        this.resolutionDueAt = ticket.getResolutionDueAt();
    }

    // Getters
    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getSubject() { return subject; }
    public String getDescription() { return description; }
    public TicketStatus getStatus() { return status; }
    public TicketPriority getPriority() { return priority; }
    public TicketChannel getChannel() { return channel; }
    public UserResponse getRequester() { return requester; }
    public UserResponse getAssignee() { return assignee; }
    public String getQueueName() { return queueName; }
    public String getSlaPolicyName() { return slaPolicyName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public LocalDateTime getClosedAt() { return closedAt; }
    public LocalDateTime getFirstResponseDueAt() { return firstResponseDueAt; }
    public LocalDateTime getResolutionDueAt() { return resolutionDueAt; }

}