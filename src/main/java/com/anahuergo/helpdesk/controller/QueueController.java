package com.anahuergo.helpdesk.controller;

import com.anahuergo.helpdesk.domain.Queue;
import com.anahuergo.helpdesk.repository.QueueRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/queues")
public class QueueController {

    private final QueueRepository queueRepository;

    public QueueController(QueueRepository queueRepository) {
        this.queueRepository = queueRepository;
    }

    @GetMapping
    public List<Queue> findAll() {
        return queueRepository.findAll();
    }

    @PostMapping
    public Queue create(@RequestBody Queue queue) {
        return queueRepository.save(queue);
    }

}