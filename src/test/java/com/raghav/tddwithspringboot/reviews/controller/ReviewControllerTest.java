package com.raghav.tddwithspringboot.reviews.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.raghav.tddwithspringboot.reviews.model.Review;
import com.raghav.tddwithspringboot.reviews.model.ReviewEntry;
import com.raghav.tddwithspringboot.reviews.service.ReviewService;
import org.junit.jupiter.api.BeforeAll;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ReviewControllerTest {
    @MockBean
    private ReviewService service;

    @Autowired
    private MockMvc mockMvc;

    /**
     * Creates a DateFormat that we can use to compare SpringMVC returned dates to expected values.
     */
    private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
    //private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss.SSSXXXXX");

    @BeforeAll
    static void beforeAll() {
        // Spring's dates are configured to GMT, so adjust to our timezone accordingly
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    @Test
    @DisplayName("GET /review/{id} - Found")
    void testGetReviewByIdFound() throws Exception {
        // Setup our mocked service
        Review mockReview = new Review("reviewId", 1, 1);
        Date now = new Date();
        mockReview.getEntries().add(new ReviewEntry("test-user", now, "Great product"));
        doReturn(Optional.of(mockReview)).when(service).findById("reviewId");

        // Execute the GET request
        mockMvc.perform(get("/review/{id}", "reviewId"))
                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.ETAG, "\"1\""))
                .andExpect(header().string(HttpHeaders.LOCATION, "/review/" + mockReview.getId()))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is("reviewId")))
                .andExpect(jsonPath("$.productId", is(1)))
                .andExpect(jsonPath("$.entries.length()", is(1)))
                .andExpect(jsonPath("$.entries[0].username", is("test-user")))
                .andExpect(jsonPath("$.entries[0].review", is("Great product")));
                //.andExpect(jsonPath("$.entries[0].date", is(df.format(now))));
    }

    @Test
    @DisplayName("GET /review/{id} - Not Found")
    void testGetReviewByIdNotFound() throws Exception {
        // Setup the mock sevice
        doReturn(Optional.empty()).when(service).findById("reviewId");

        // Execute the GET request
        mockMvc.perform(get("/review/{id}", "reviewId"))
                // Validate the response code
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /review - Success")
    void testCreateReview() throws Exception {
        // Setup mock service
        Date now = new Date();
        Review postReview = new Review(1);
        postReview.getEntries().add(new ReviewEntry("test-user", now, "Great product"));

        Review mockReview = new Review("reviewId", 1, 1);
        mockReview.getEntries().add(new ReviewEntry("test-user", now, "Great product"));

        doReturn(mockReview).when(service).save(any());

        mockMvc.perform(post("/review")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(asJsonString(mockReview)))

                //Validate the response code and content type
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                // Validate the returned fields
                .andExpect(jsonPath("$.id", is("reviewId")))
                .andExpect(jsonPath("$.productId", is(1)))
                .andExpect(jsonPath("$.entries.length()", is(1)))
                .andExpect(jsonPath("$.entries[0].username", is("test-user")))
                .andExpect(jsonPath("$.entries[0].review", is("Great product")));
                //.andExpect(jsonPath("$.entries[0].date", is(df.format(now))));
    }

    @Test
    @DisplayName("POST /review/{productId}/entry - Success")
    void testAddEntryToReview() throws Exception {
        // Setup mock service
        Date now = new Date();
        ReviewEntry reviewEntry = new ReviewEntry("test-user", now, "Great product");
        Review mockReview = new Review("1", 1, 1);
        Review returnedReview = new Review("1", 1, 2);
        returnedReview.getEntries().add(reviewEntry);

        // Handle lookup
        doReturn(Optional.of(mockReview)).when(service).findByProductId(1);

        // Handle save
        doReturn(returnedReview).when(service).save(any());

        mockMvc.perform(post("/review/{productId}/entry", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(reviewEntry)))

                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.ETAG, "\"2\""))
                .andExpect(header().string(HttpHeaders.LOCATION, "/review/1"))
                // Validate the returned fields
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.productId", is(1)))
                .andExpect(jsonPath("$.entries.length()", is(1)))
                .andExpect(jsonPath("$.entries[0].username", is("test-user")))
                .andExpect(jsonPath("$.entries[0].review", is("Great product")));
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
