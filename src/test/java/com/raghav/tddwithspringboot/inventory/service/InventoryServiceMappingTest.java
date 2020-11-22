package com.raghav.tddwithspringboot.inventory.service;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.raghav.tddwithspringboot.inventory.model.InventoryRecord;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
public class InventoryServiceMappingTest {
    @Autowired
    private InventoryService service;

    private WireMockServer wireMockServer;

    @BeforeEach
    void beforeEach() {
        // Start the WireMock Server
        wireMockServer = new WireMockServer(9999);
        wireMockServer.start();
    }

    @AfterEach
    void afterEach() {
        wireMockServer.stop();
    }

    @Test
    @DisplayName("Test getInventoryRecord() - Success")
    void testGetInventorRecordSuccess() {
        Optional<InventoryRecord> record = service.getInventoryRecord(1);
        Assertions.assertTrue(record.isPresent(), "InventoryRecord should be present");

        // Validate the contents of the response
        Assertions.assertEquals(500, record.get().getQuantity(), "The quantity should be 500");
    }

    @Test
    @DisplayName("Test getInventoryRecord() - Failure")
    void testGetInventorRecordFailure() {
        Optional<InventoryRecord> record = service.getInventoryRecord(2);
        Assertions.assertFalse(record.isPresent(), "InventoryRecord should not be present");
    }

    @Test
    @DisplayName("Test purchaseProduct - Success")
    void testPurchaseProductSuccess() {
        Optional<InventoryRecord> record = service.purchaseProduct(1, 1);
        Assertions.assertTrue(record.isPresent(), "InventoryRecord should be present");

        // Validate the contents of the response
        Assertions.assertEquals(499, record.get().getQuantity().intValue(), "The quantity should be 499");
    }
}
