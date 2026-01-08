package com.anahuergo.helpdesk.security;

import com.anahuergo.helpdesk.domain.User;
import com.anahuergo.helpdesk.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElse(null);
    }

    public Long getCurrentTenantId() {
        User user = getCurrentUser();
        if (user != null && user.getTenant() != null) {
            return user.getTenant().getId();
        }
        return null;
    }

}