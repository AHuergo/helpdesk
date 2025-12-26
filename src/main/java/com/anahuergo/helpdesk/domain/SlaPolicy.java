package com.anahuergo.helpdesk.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "sla_policies")
public class SlaPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private Integer firstResponseMinutes;

    @Column(nullable = false)
    private Integer resolutionMinutes;

    @Column(nullable = false)
    private boolean useBusinessHours = true;

    @Column(nullable = false)
    private boolean active = true;

}