package com.anahuergo.helpdesk.controller;

import com.anahuergo.helpdesk.domain.User;
import com.anahuergo.helpdesk.domain.UserRole;
import com.anahuergo.helpdesk.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class UserControllerTest {

    @Autowired
    private UserController userController;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldCreateUser() {
        User user = new User();
        user.setEmail("nuevo@test.com");
        user.setPassword("12345678");
        user.setName("Nuevo");
        user.setSurname("Usuario");

        var response = userController.create(user);

        assertNotNull(response);
        assertEquals("nuevo@test.com", response.getEmail());
        assertEquals("Nuevo", response.getName());
        assertEquals(UserRole.REQUESTER, response.getRole());
    }

    @Test
    void shouldCreateAgent() {
        User user = new User();
        user.setEmail("agente@test.com");
        user.setPassword("12345678");
        user.setName("Agente");
        user.setSurname("Soporte");
        user.setRole(UserRole.AGENT);

        var response = userController.create(user);

        assertEquals(UserRole.AGENT, response.getRole());
    }

    @Test
    void shouldFindUserById() {
        User user = new User();
        user.setEmail("buscar@test.com");
        user.setPassword("12345678");
        user.setName("Buscar");
        user.setSurname("Test");
        user = userRepository.save(user);

        var found = userController.findById(user.getId());

        assertNotNull(found);
        assertEquals("buscar@test.com", found.getEmail());
    }

    @Test
    void shouldListAllUsers() {
        User user1 = new User();
        user1.setEmail("user1@test.com");
        user1.setPassword("12345678");
        user1.setName("User1");
        user1.setSurname("Test");
        userRepository.save(user1);

        User user2 = new User();
        user2.setEmail("user2@test.com");
        user2.setPassword("12345678");
        user2.setName("User2");
        user2.setSurname("Test");
        userRepository.save(user2);

        var results = userController.findAll();

        assertTrue(results.size() >= 2);
    }

}