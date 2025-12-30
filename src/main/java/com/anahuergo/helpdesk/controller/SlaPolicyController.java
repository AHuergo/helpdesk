package com.anahuergo.helpdesk.controller;

import com.anahuergo.helpdesk.domain.SlaPolicy;
import com.anahuergo.helpdesk.repository.SlaPolicyRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/sla-policies")
public class SlaPolicyController {

    private final SlaPolicyRepository slaPolicyRepository;

    public SlaPolicyController(SlaPolicyRepository slaPolicyRepository) {
        this.slaPolicyRepository = slaPolicyRepository;
    }

    @GetMapping
    public List<SlaPolicy> findAll() {
        return slaPolicyRepository.findAll();
    }

    @PostMapping
    public SlaPolicy create(@RequestBody SlaPolicy policy) {
        return slaPolicyRepository.save(policy);
    }

}