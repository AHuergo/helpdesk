package com.anahuergo.helpdesk.controller;

import com.anahuergo.helpdesk.domain.SlaPolicy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class SlaPolicyControllerTest {

    @Autowired
    private SlaPolicyController slaPolicyController;

    @Test
    void shouldCreateSlaPolicy() {
        SlaPolicy policy = new SlaPolicy();
        policy.setName("Premium SLA " + UUID.randomUUID());
        policy.setDescription("SLA para clientes premium");
        policy.setFirstResponseMinutes(30);
        policy.setResolutionMinutes(240);

        var created = slaPolicyController.create(policy);

        assertNotNull(created);
        assertEquals(30, created.getFirstResponseMinutes());
        assertEquals(240, created.getResolutionMinutes());
        assertTrue(created.isActive());
    }

    @Test
    void shouldListAllPolicies() {
        SlaPolicy policy1 = new SlaPolicy();
        policy1.setName("Basic " + UUID.randomUUID());
        policy1.setFirstResponseMinutes(120);
        policy1.setResolutionMinutes(960);
        slaPolicyController.create(policy1);

        SlaPolicy policy2 = new SlaPolicy();
        policy2.setName("Enterprise " + UUID.randomUUID());
        policy2.setFirstResponseMinutes(15);
        policy2.setResolutionMinutes(120);
        slaPolicyController.create(policy2);

        var policies = slaPolicyController.findAll();

        assertTrue(policies.size() >= 2);
    }

}