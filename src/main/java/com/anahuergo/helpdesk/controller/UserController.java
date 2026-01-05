package com.anahuergo.helpdesk.controller;

import com.anahuergo.helpdesk.domain.User;
import com.anahuergo.helpdesk.dto.UserResponse;
import com.anahuergo.helpdesk.repository.UserRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(UserResponse::new)
                .toList();
    }

    @GetMapping("/{id}")
    public UserResponse findById(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow();
        return new UserResponse(user);
    }

    @PostMapping
    public UserResponse create(@RequestBody User user) {
        User saved = userRepository.save(user);
        return new UserResponse(saved);
    }

}