package com.anahuergo.helpdesk.repository;

import com.anahuergo.helpdesk.domain.Queue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QueueRepository extends JpaRepository<Queue, Long> {

}
