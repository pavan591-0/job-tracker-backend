package com.example.jobtracker.controller;

import com.example.jobtracker.dto.JobApplicationResponseDTO;
import com.example.jobtracker.exception.NotFoundException;
import com.example.jobtracker.service.JobApplicationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;



@WebMvcTest(JobApplicationController.class)
public class JobApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JobApplicationService jobApplicationService;

    @Test
    void contextLoads(){

    }

    private JobApplicationResponseDTO sampleResponse() {
        return new JobApplicationResponseDTO(
                1L,
                "Stark Industries",
                "Backend Engineer",
                "APPLIED",
                "New York",
                "LinkedIn",
                "Good fit",
                LocalDate.now(),
                LocalDate.now()
        );
    }

    @Test
    void shouldReturnJobApplicationById() throws Exception {

        // given
        when(jobApplicationService.getById(1L))
                .thenReturn(sampleResponse());

        // when + then
        mockMvc.perform(get("/api/job-applications/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.companyName").value("Stark Industries"))
                .andExpect(jsonPath("$.position").value("Backend Engineer"))
                .andExpect(jsonPath("$.status").value("APPLIED"));
    }

    @Test
    void shouldReturn404WhenJobNotFound() throws Exception {

        // given
        when(jobApplicationService.getById(230L))
                .thenThrow(new NotFoundException("Job not found with id 230"));

        // when + then
        mockMvc.perform(get("/api/job-applications/230"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Job not found with id 230"));
    }


}
