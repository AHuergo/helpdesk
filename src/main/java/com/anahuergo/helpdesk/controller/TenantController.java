package com.anahuergo.helpdesk.controller;

import com.anahuergo.helpdesk.domain.Tenant;
import com.anahuergo.helpdesk.repository.TenantRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tenants")
public class TenantController {

    private final TenantRepository tenantRepository;

    public TenantController(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Tenant> findAll() {
        return tenantRepository.findAll();
    }

    @PostMapping
    public Tenant create(@RequestBody Tenant tenant) {
        return tenantRepository.save(tenant);
    }

}