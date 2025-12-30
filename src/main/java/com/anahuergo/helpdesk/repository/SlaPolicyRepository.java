package com.anahuergo.helpdesk.repository;

import com.anahuergo.helpdesk.domain.SlaPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SlaPolicyRepository extends JpaRepository<SlaPolicy, Long> {

}