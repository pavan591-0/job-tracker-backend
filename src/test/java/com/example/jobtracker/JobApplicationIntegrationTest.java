package com.example.jobtracker;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class JobApplicationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateJobApplication_EndToEnd() throws Exception {

        mockMvc.perform(
                        post("/api/job-applications")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                        {
                          "companyName": "Netflix",
                          "position": "Backend Engineer",
                          "status": "APPLIED",
                          "location": "Los Angeles",
                          "source": "Career Page",
                          "notes": "Streaming scale"
                        }
                    """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.companyName").value("Netflix"));
    }

    @Test
    void shouldReturn400_EndToEnd_WhenValidationFails() throws Exception {
        mockMvc.perform(
                        post("/api/job-applications")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                        {
                          "companyName": "",
                          "position": "Backend Engineer",
                          "status": "APPLIED",
                          "location": "Los Angeles",
                          "source": "LinkedIn",
                          "notes": "x"
                        }
                    """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.companyName").exists());
    }




}

