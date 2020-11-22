package com.raghav.tddwithspringboot.products;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.database.rider.core.api.connection.ConnectionHolder;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.DBUnitExtension;
import com.raghav.tddwithspringboot.products.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith({DBUnitExtension.class, SpringExtension.class})
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ProductServiceIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DataSource dataSource;

    public ConnectionHolder getConnectionHolder() {
        // Return a function that retrieves a connection from our datasource
        return () -> dataSource.getConnection();
    }

    @Test
    @DisplayName("GET /product/1 - Found")
    @DataSet("products.yml")
    void testGetProductByIdFound() throws Exception {
        // Execute the GET request
        mockMvc.perform(get("/product/{id}", 1))
                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate headers
                .andExpect(header().string(HttpHeaders.ETAG, "\"1\""))
                .andExpect(header().string(HttpHeaders.LOCATION, "/product/1"))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Product 1")))
                .andExpect(jsonPath("$.quantity", is(10)))
                .andExpect(jsonPath("$.version", is(1)));

    }
    @Test
    @DisplayName("GET /product/1 - Not Found")
    @DataSet("products.yml")
    void testGetProductByIdNotFound() throws Exception {
        // Execute the GET request
        mockMvc.perform(get("/product/{1}", 99))
                // Validate that we get a 404 not found response
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /product - Success")
    @DataSet("products.yml")
    void testCreateProduct() throws Exception {
        Product postProduct = new Product("Product Name", 10);

        mockMvc.perform(post("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postProduct)))
                // Validate the response code and content type
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.ETAG, "\"1\""))
                .andExpect(header().string(HttpHeaders.LOCATION, "/product/3"))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.name", is("Product Name")))
                .andExpect(jsonPath("$.quantity", is(10)))
                .andExpect(jsonPath("$.version", is(1)));
    }

    @Test
    @DisplayName("PUT /product/1 - Success")
    @DataSet("products.yml")
    void testProductPutSuccess() throws Exception {
        Product putProduct = new Product("Product Name", 10);

        mockMvc.perform(put("/product/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.IF_MATCH, 1)
                .content(asJsonString(putProduct)))

                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.ETAG, "\"2\""))
                .andExpect(header().string(HttpHeaders.LOCATION, "/product/1"))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Product Name")))
                .andExpect(jsonPath("$.quantity", is(10)))
                .andExpect(jsonPath("$.version", is(2)));
    }

    @Test
    @DisplayName("PUT /product/1 - Version Mismatch")
    @DataSet("products.yml")
    void testProductPutVersionMismatch() throws Exception {
        Product putProduct = new Product("Product Name", 10);

        mockMvc.perform(put("/product/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.IF_MATCH, 7)
                .content(asJsonString(putProduct)))

                // Validate the response code and content type
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("PUT /product/1 - Not Found")
    @DataSet("products.yml")
    void testProductPutNotFound() throws Exception {
        Product putProduct = new Product("Product Name", 10);

        mockMvc.perform(put("/product/{id}", 99)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.IF_MATCH, 1)
                .content(asJsonString(putProduct)))

                // Validate the response code and content type
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /product/1 - Success")
    @DataSet("products.yml")
    void testProductDeleteSuccess() throws Exception {
        mockMvc.perform(delete("/product/{id}", 1))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /product/1 - Not Found")
    @DataSet("products.yml")
    void testProductDeleteNotFound() throws Exception {
        mockMvc.perform(delete("/product/{id}", 99))
                .andExpect(status().isNotFound());
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
