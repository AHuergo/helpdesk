package com.anahuergo.helpdesk.dto;

import com.anahuergo.helpdesk.domain.TicketEvent;
import java.time.LocalDateTime;

public class TicketEventResponse {

    private Long id;
    private String eventType;
    private String oldValue;
    private String newValue;
    private String userName;
    private LocalDateTime createdAt;

    public TicketEventResponse(TicketEvent event) {
        this.id = event.getId();
        this.eventType = event.getEventType();
        this.oldValue = event.getOldValue();
        this.newValue = event.getNewValue();
        this.userName = event.getUser() != null ? event.getUser().getName() : "Sistema";
        this.createdAt = event.getCreatedAt();
    }

    // Getters
    public Long getId() { return id; }
    public String getEventType() { return eventType; }
    public String getOldValue() { return oldValue; }
    public String getNewValue() { return newValue; }
    public String getUserName() { return userName; }
    public LocalDateTime getCreatedAt() { return createdAt; }

}