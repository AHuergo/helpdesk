package com.anahuergo.helpdesk.controller;

import com.anahuergo.helpdesk.domain.Queue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class QueueControllerTest {

    @Autowired
    private QueueController queueController;

    @Test
    void shouldCreateQueue() {
        Queue queue = new Queue();
        queue.setName("Soporte Tecnico " + UUID.randomUUID());
        queue.setDescription("Problemas tecnicos");

        var created = queueController.create(queue);

        assertNotNull(created);
        assertTrue(created.getName().startsWith("Soporte Tecnico"));
        assertTrue(created.isActive());
    }

    @Test
    void shouldListAllQueues() {
        Queue queue1 = new Queue();
        queue1.setName("Ventas " + UUID.randomUUID());
        queue1.setDescription("Consultas de ventas");
        queueController.create(queue1);

        Queue queue2 = new Queue();
        queue2.setName("Facturacion " + UUID.randomUUID());
        queue2.setDescription("Problemas con facturas");
        queueController.create(queue2);

        var queues = queueController.findAll();

        assertTrue(queues.size() >= 2);
    }

}