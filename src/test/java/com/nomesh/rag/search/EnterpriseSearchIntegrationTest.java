package com.nomesh.rag.search;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifies the enterprise search API through the Spring Boot web layer.
 *
 * <p>The test confirms that request binding, validation, controller orchestration,
 * and API response formatting work together correctly.</p>
 *
 * @author Nomesh De Silva
 */
@SpringBootTest
@AutoConfigureMockMvc
class EnterpriseSearchIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private tools.jackson.databind.ObjectMapper objectMapper;

    @Test
    void shouldRejectBlankSearchQuery() throws Exception {

        EnterpriseSearchRequest request =
                new EnterpriseSearchRequest(
                        "   ",
                        10,
                        0.5,
                        null
                );

        mockMvc.perform(
                        post("/api/v1/search")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("INVALID_SEARCH_REQUEST"))
                .andExpect(jsonPath("$.message")
                        .value("Search query must not be blank."));
    }

    @Test
    void shouldRejectInvalidTopK() throws Exception {

        EnterpriseSearchRequest request =
                new EnterpriseSearchRequest(
                        "annual leave",
                        0,
                        0.5,
                        null
                );

        mockMvc.perform(
                        post("/api/v1/search")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("INVALID_SEARCH_REQUEST"));
    }
}