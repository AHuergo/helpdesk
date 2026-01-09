package com.anahuergo.helpdesk.repository;

import com.anahuergo.helpdesk.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findBySlug(String slug);

}