package com.example.jobtracker.controller;

import com.example.jobtracker.dto.JobApplicationRequestDTO;
import com.example.jobtracker.dto.JobApplicationResponseDTO;
import com.example.jobtracker.exception.BadRequestException;
import com.example.jobtracker.exception.GlobalExceptionHandler;
import com.example.jobtracker.exception.NotFoundException;
import com.example.jobtracker.service.JobApplicationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


// Controller slice tests: validate request/response contracts using MockMvc.
// Service layer is mocked to isolate controller behavior.

@WebMvcTest(JobApplicationController.class)
@Import(GlobalExceptionHandler.class)
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

    private JobApplicationResponseDTO mockResponse =
            new JobApplicationResponseDTO(
                    1L,
                    "Google",
                    "Backend Engineer",
                    "APPLIED",
                    "Mountain View",
                    "LinkedIn",
                    "Good role",
                    LocalDate.now(),
                    LocalDate.now()
            );


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

    @Test
    void shouldReturnUpdatedStatusWJobObject() throws Exception{
        when(jobApplicationService.updateStatus(1L, "APPLIED"))
                .thenReturn(sampleResponse());

        // when + then
        mockMvc.perform(patch("/api/job-applications/1/status").param("value", "APPLIED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.companyName").value("Stark Industries"))
                .andExpect(jsonPath("$.position").value("Backend Engineer"))
                .andExpect(jsonPath("$.status").value("APPLIED"));

    }

    @Test
    void shouldReturn400WhenInvalidBody() throws Exception {

        // given
        when(jobApplicationService.update(eq(2L), any(JobApplicationRequestDTO.class)))
                .thenThrow(new BadRequestException("Job fields are not properly sent"));


        // when + then
        mockMvc.perform(put("/api/job-applications/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "companyName": "Stark Industries",
                                    "position": "Backend Engineer",
                                    "status": "APPLIED",
                                   "location": "New York",
                                    "source": "LinkedIn",
                                    "notes": "Strong match for backend systems and distributed services."
                                }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Job fields are not properly sent"));
    }

    @Test
    void shouldReturn400WhenValidationFails() throws Exception {

        mockMvc.perform(
                        post("/api/job-applications")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                        {
                          "companyName": "",
                          "position": "",
                          "status": "",
                          "location": "",
                          "source": "",
                          "notes": "ok"
                        }
                    """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors.companyName")
                .value("Company name cannot be empty"))
                .andExpect(jsonPath("$.validationErrors.position")
                        .value("Position cannot be empty"))
                .andExpect(jsonPath("$.validationErrors.status")
                        .value("Status cannot be empty"))
                .andExpect(jsonPath("$.validationErrors.location")
                        .value("Location is required"))
                .andExpect(jsonPath("$.validationErrors.source")
                        .value("Source is required"));
    }

    @Test
    void shouldCreateJobApplicationSuccessfully() throws Exception {

        when(jobApplicationService.create(any(JobApplicationRequestDTO.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(
                        post("/api/job-applications")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                        {
                          "companyName": "Google",
                          "position": "Backend Engineer",
                          "status": "APPLIED",
                          "location": "Mountain View",
                          "source": "LinkedIn",
                          "notes": "Good role"
                        }
                    """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.companyName").value("Google"))
                .andExpect(jsonPath("$.position").value("Backend Engineer"))
                .andExpect(jsonPath("$.status").value("APPLIED"));
    }



}
