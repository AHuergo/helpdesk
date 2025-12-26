package com.anahuergo.helpdesk.repository;

import com.anahuergo.helpdesk.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}