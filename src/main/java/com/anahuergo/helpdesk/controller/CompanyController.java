package com.anahuergo.helpdesk.controller;

import com.anahuergo.helpdesk.domain.Company;
import com.anahuergo.helpdesk.repository.CompanyRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyRepository companyRepository;

    public CompanyController(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Company> findAll() {
        return companyRepository.findAll();
    }

    @PostMapping
    public Company create(@RequestBody Company company) {
        return companyRepository.save(company);
    }

}