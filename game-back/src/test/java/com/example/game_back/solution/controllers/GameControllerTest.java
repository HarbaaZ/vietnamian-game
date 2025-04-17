package com.example.game_back.solution.controllers;

import com.example.game_back.solution.models.Solution;
import com.example.game_back.solution.services.GameSolverService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class GameControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private GameSolverService gameSolverService;

    @InjectMocks
    private GameController gameController;

    private Solution testSolution;
    private List<Integer> testNumbers;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(gameController).build();
        
        testNumbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        testSolution = new Solution(1L, testNumbers);
        testSolution.setCorrect(false);
    }

    @Test
    void getAllSolutions_shouldReturnAllSolutions() throws Exception {
        List<Solution> solutions = Collections.singletonList(testSolution);
        when(gameSolverService.getAllSolutions()).thenReturn(solutions);

        mockMvc.perform(get("/api/game/solutions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].numbers", is(testNumbers)))
                .andExpect(jsonPath("$[0].correct", is(false)));

        verify(gameSolverService).getAllSolutions();
    }

    @Test
    void getSolutionById_whenSolutionExists_shouldReturnSolution() throws Exception {
        when(gameSolverService.getSolutionById(1L)).thenReturn(Optional.of(testSolution));

        mockMvc.perform(get("/api/game/solutions/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.numbers", is(testNumbers)))
                .andExpect(jsonPath("$.correct", is(false)));

        verify(gameSolverService).getSolutionById(1L);
    }

    @Test
    void getSolutionById_whenSolutionDoesNotExist_shouldReturnNotFound() throws Exception {
        when(gameSolverService.getSolutionById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/game/solutions/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(gameSolverService).getSolutionById(999L);
    }

    @Test
    void createSolution_withValidNumbers_shouldReturnCreatedSolution() throws Exception {
        when(gameSolverService.createSolution(testNumbers)).thenReturn(testSolution);

        mockMvc.perform(post("/api/game/solutions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testNumbers)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.numbers", is(testNumbers)))
                .andExpect(jsonPath("$.correct", is(false)));

        verify(gameSolverService).createSolution(testNumbers);
    }

    @Test
    void createSolution_withInvalidNumbers_shouldReturnBadRequest() throws Exception {
        List<Integer> invalidNumbers = Arrays.asList(1, 2, 3);

        mockMvc.perform(post("/api/game/solutions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidNumbers)))
                .andExpect(status().isBadRequest());

        verify(gameSolverService, never()).createSolution(any());
    }

    @Test
    void deleteSolutionById_whenSolutionExists_shouldReturnNoContent() throws Exception {
        when(gameSolverService.deleteSolutionById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/game/solutions/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(gameSolverService).deleteSolutionById(1L);
    }

    @Test
    void deleteSolutionById_whenSolutionDoesNotExist_shouldReturnNotFound() throws Exception {
        when(gameSolverService.deleteSolutionById(anyLong())).thenReturn(false);

        mockMvc.perform(delete("/api/game/solutions/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(gameSolverService).deleteSolutionById(999L);
    }

    @Test
    void deleteAllSolutions_shouldReturnDeletedCount() throws Exception {
        when(gameSolverService.deleteAllSolutions()).thenReturn(5L);

        mockMvc.perform(delete("/api/game/solutions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deletedCount", is(5)));

        verify(gameSolverService).deleteAllSolutions();
    }

    @Test
    void generateSolutions_shouldReturnGenerationStats() throws Exception {
        when(gameSolverService.generateSolutionsEfficient()).thenReturn(100L);
        when(gameSolverService.getAllSolutions()).thenReturn(Collections.singletonList(testSolution));

        mockMvc.perform(get("/api/game/solutions/generate")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.calculationTimeMs", is(100)))
                .andExpect(jsonPath("$.totalSolutions", is(1)));

        verify(gameSolverService).generateSolutionsEfficient();
        verify(gameSolverService).getAllSolutions();
    }

    @Test
    void updateSolution_whenSolutionExists_shouldReturnUpdatedSolution() throws Exception {
        List<Integer> newNumbers = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1);
        Solution updatedSolution = new Solution(1L, newNumbers);
        updatedSolution.setCorrect(false);
        
        when(gameSolverService.updateSolution(1L, newNumbers)).thenReturn(Optional.of(updatedSolution));

        mockMvc.perform(put("/api/game/solutions/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newNumbers)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.numbers", is(newNumbers)))
                .andExpect(jsonPath("$.correct", is(false)));

        verify(gameSolverService).updateSolution(1L, newNumbers);
    }

    @Test
    void updateSolution_whenSolutionDoesNotExist_shouldReturnNotFound() throws Exception {
        when(gameSolverService.updateSolution(anyLong(), any())).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/game/solutions/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testNumbers)))
                .andExpect(status().isNotFound());

        verify(gameSolverService).updateSolution(999L, testNumbers);
    }

    @Test
    void updateSolution_withInvalidNumbers_shouldReturnBadRequest() throws Exception {
        List<Integer> invalidNumbers = Arrays.asList(1, 2, 3);

        mockMvc.perform(put("/api/game/solutions/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidNumbers)))
                .andExpect(status().isBadRequest());

        verify(gameSolverService, never()).updateSolution(anyLong(), any());
    }
}
