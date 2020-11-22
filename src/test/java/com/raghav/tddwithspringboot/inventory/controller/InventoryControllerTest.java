package com.raghav.tddwithspringboot.inventory.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.raghav.tddwithspringboot.inventory.model.InventoryRecord;
import com.raghav.tddwithspringboot.inventory.model.PurchaseRecord;
import com.raghav.tddwithspringboot.inventory.service.InventoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;


import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class InventoryControllerTest {
    @MockBean
    private InventoryService service;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /inventory/1 - Success")
    void testGetInventoryByIdSuccess() throws Exception {
        // Setup our mocked service
        InventoryRecord mockRecord = new InventoryRecord(1, 10, "Product 1", "Great Products");
        doReturn(Optional.of(mockRecord)).when(service).getInventoryRecord(1);

        // Execute the GET request
        mockMvc.perform(get("/inventory/{id}", 1))
                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.LOCATION, "/inventory/1"))

                // Valiadate the return fields
                .andExpect(jsonPath("$.productId", is(1)))
                .andExpect(jsonPath("$.quantity", is(10)))
                .andExpect(jsonPath("$.productName", is("Product 1")))
                .andExpect(jsonPath("$.productCategory", is("Great Products")));
    }

    @Test
    @DisplayName("GET /inventory/2 - Failure")
    void testGetInventoryByIdFailure() throws Exception {
        // Setup our mocked service
        doReturn(Optional.empty()).when(service).getInventoryRecord(2);

        // Execute the GET request
        mockMvc.perform(get("/inventory/{id}", 2))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /inventory/purchase-record - Success")
    void testCreatePurchaseRecord() throws Exception {
        // Setup mocked service
        InventoryRecord mockRecord = new InventoryRecord(1, 10, "Product 1", "Great Products");
        doReturn(Optional.of(mockRecord)).when(service).purchaseProduct(1, 5);

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
                .andExpect(jsonPath("$.quantity", is(10)))
                .andExpect(jsonPath("$.productName", is("Product 1")))
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
