package com.raghav.tddwithspringboot.inventory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.raghav.tddwithspringboot.inventory.model.PurchaseRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
@AutoConfigureMockMvc
public class InventoryServiceIntgrationTest {
    @Autowired
    private MockMvc mockMvc;

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
    @DisplayName("GET /inventory/1 - Success")
    void testGetInventoryByIdSuccess() throws Exception {
        // Execute the GET request
        mockMvc.perform(get("/inventory/{id}", 1))
                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.LOCATION, "/inventory/1"))

                // Valiadate the return fields
                .andExpect(jsonPath("$.productId", is(1)))
                .andExpect(jsonPath("$.quantity", is(500)))
                .andExpect(jsonPath("$.productName", is("Super Great Product")))
                .andExpect(jsonPath("$.productCategory", is("Great Products")));
    }

    @Test
    @DisplayName("GET /inventory/2 - Failure")
    void testGetInventoryByIdFailure() throws Exception {
        // Execute the GET request
        mockMvc.perform(get("/inventory/{id}", 2))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /inventory/purchase-record - Success")
    void testCreatePurchaseRecord() throws Exception {
        mockMvc.perform(post("/inventory/purchase-record")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(new PurchaseRecord(1, 5))))
                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.LOCATION, "/inventory/1"))

                // Validate the return fields
                .andExpect(jsonPath("$.productId", is(1)))
                .andExpect(jsonPath("$.quantity", is(499)))
                .andExpect(jsonPath("$.productName", is("Super Great Product")))
                .andExpect(jsonPath("$.productCategory", is("Great Products")));
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
