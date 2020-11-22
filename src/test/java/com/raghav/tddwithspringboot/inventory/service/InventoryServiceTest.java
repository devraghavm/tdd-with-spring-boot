package com.raghav.tddwithspringboot.inventory.service;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.raghav.tddwithspringboot.inventory.model.InventoryRecord;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;


@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
public class InventoryServiceTest {
    @Autowired
    private InventoryService service;

    private WireMockServer wireMockServer;

    @BeforeEach
    void beforeEach() {
        // Start the WireMock Server
        wireMockServer = new WireMockServer(9999);
        wireMockServer.start();

        // Configure our requests
        wireMockServer.stubFor(get(urlEqualTo("/inventory/1"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBodyFile("json/inventory-response.json")));
        wireMockServer.stubFor(get(urlEqualTo("/inventory/2"))
                .willReturn(aResponse().withStatus(404)));
        wireMockServer.stubFor(post(urlEqualTo("/inventory/1/purchaseRecord"))
                // Actual header sent by RestTemplate is: application/json;charset=UTF-8
                .withHeader("Content-Type", containing("application/json"))
                .withRequestBody(containing("\"productId\":1"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBodyFile("json/inventory-response-after-post.json")));
    }

    @AfterEach
    void afterEach() {
        wireMockServer.stop();
    }

    @Test
    @DisplayName("Test getInventoryRecord() - Success")
    void testGetInventoryRecordSuccess() {
        Optional<InventoryRecord> record = service.getInventoryRecord(1);
        Assertions.assertTrue(record.isPresent(), "InventoryRecord should be present");

        // Validate the contents of the response
        Assertions.assertEquals(500, record.get().getQuantity(), "The quantity should be 500");
    }

    @Test
    @DisplayName("Test getInventoryRecord() - Failure")
    void testGetInventoryRecordFailure() {
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
