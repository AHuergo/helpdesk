package com.anahuergo.helpdesk.domain;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import lombok.Data;

@Data
@Entity
@Table(name="tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @NotBlank(message = "El asunto es obligatorio")
    @Size(max = 200, message = "El asunto no puede superar 200 caracteres")
    @Column(nullable = false)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status = TicketStatus.NEW;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketPriority priority = TicketPriority.NORMAL;

    @Enumerated(EnumType.STRING)
    private TicketChannel channel = TicketChannel.WEB;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "requester_id", nullable = false)
    private User requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private User assignee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="queue_id")
    private Queue queue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sla_policy_id")
    private SlaPolicy slaPolicy;

    private LocalDateTime firstResponseDueAt;
    private LocalDateTime resolutionDueAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;
    private LocalDateTime closedAt;

    public void setSubject(String subject) {
        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("Subject cannot be empty");
        }
        this.subject = subject;
    }

}


