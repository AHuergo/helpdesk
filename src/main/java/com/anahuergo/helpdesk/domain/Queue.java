package com.anahuergo.helpdesk.domain;

import jakarta.persistence.*; 
import lombok.Data;

    
@Data
@Entity
@Table(name="queues")
public class Queue {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(unique=true, nullable=false)
    private String name;

    private String description;

    @Column(nullable=false)
    private boolean active = true;

}
