package com.anahuergo.helpdesk.controller;

import com.anahuergo.helpdesk.domain.Tenant;
import com.anahuergo.helpdesk.domain.User;
import com.anahuergo.helpdesk.dto.UserResponse;
import com.anahuergo.helpdesk.repository.TenantRepository;
import com.anahuergo.helpdesk.repository.UserRepository;
import com.anahuergo.helpdesk.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, 
                          TenantRepository tenantRepository,
                          JwtService jwtService, 
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tenantRepository = tenantRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public UserResponse register(@RequestBody User user, 
                                 @RequestParam(required = false) Long tenantId,
                                 @RequestParam(required = false) String tenantName) {
        
        // Asignar tenant existente o crear uno nuevo
        if (tenantId != null) {
            Tenant tenant = tenantRepository.findById(tenantId).orElseThrow();
            user.setTenant(tenant);
        } else if (tenantName != null) {
            Tenant tenant = new Tenant();
            tenant.setName(tenantName);
            tenant.setSlug(tenantName.toLowerCase().replaceAll(" ", "-"));
            tenant.setPlan("free");
            tenant = tenantRepository.save(tenant);
            user.setTenant(tenant);
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User saved = userRepository.save(user);
        return new UserResponse(saved);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"));
        }

        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());

        return ResponseEntity.ok(Map.of(
            "token", token,
            "user", new UserResponse(user)
        ));
    }

}