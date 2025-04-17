package com.example.game_back.solution.services;

import com.example.game_back.solution.models.Solution;
import com.example.game_back.solution.repositories.SolutionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameSolverServiceTest {

    @Mock
    private SolutionRepository solutionRepository;

    private GameSolverService gameSolverService;

    private Solution testSolution;
    private List<Integer> testNumbers;

    @BeforeEach
    void setUp() {
        testNumbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        testSolution = new Solution(1L, testNumbers);
        gameSolverService = new GameSolverService(solutionRepository);
    }

    @Test
    void getAllSolutions_shouldReturnAllSolutions() {
        List<Solution> expectedSolutions = Collections.singletonList(testSolution);
        when(solutionRepository.findAll()).thenReturn(expectedSolutions);

        List<Solution> actualSolutions = gameSolverService.getAllSolutions();

        assertEquals(expectedSolutions, actualSolutions);
        verify(solutionRepository).findAll();
    }

    @Test
    void getSolutionById_whenSolutionExists_shouldReturnSolution() {
        when(solutionRepository.findById(1L)).thenReturn(Optional.of(testSolution));

        Optional<Solution> result = gameSolverService.getSolutionById(1L);

        assertTrue(result.isPresent());
        assertEquals(testSolution, result.get());
        verify(solutionRepository).findById(1L);
    }

    @Test
    void getSolutionById_whenSolutionDoesNotExist_shouldReturnEmpty() {
        when(solutionRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<Solution> result = gameSolverService.getSolutionById(999L);

        assertTrue(result.isEmpty());
        verify(solutionRepository).findById(999L);
    }

    @Test
    void createSolution_whenSolutionDoesNotExist_shouldCreateAndReturnSolution() {
        when(solutionRepository.findAll()).thenReturn(Collections.emptyList());
        when(solutionRepository.save(any(Solution.class))).thenReturn(testSolution);

        Solution result = gameSolverService.createSolution(testNumbers);

        assertEquals(testSolution, result);
        verify(solutionRepository).findAll();
        verify(solutionRepository).save(any(Solution.class));
    }

    @Test
    void createSolution_whenSolutionExists_shouldReturnExistingSolution() {
        when(solutionRepository.findAll()).thenReturn(Collections.singletonList(testSolution));

        Solution result = gameSolverService.createSolution(testNumbers);

        assertEquals(testSolution, result);
        verify(solutionRepository).findAll();
        verify(solutionRepository, never()).save(any(Solution.class));
    }

    @Test
    void deleteSolutionById_whenSolutionExists_shouldReturnTrue() {
        when(solutionRepository.existsById(1L)).thenReturn(true);
        doNothing().when(solutionRepository).deleteById(1L);

        boolean result = gameSolverService.deleteSolutionById(1L);

        assertTrue(result);
        verify(solutionRepository).existsById(1L);
        verify(solutionRepository).deleteById(1L);
    }

    @Test
    void deleteSolutionById_whenSolutionDoesNotExist_shouldReturnFalse() {
        when(solutionRepository.existsById(anyLong())).thenReturn(false);

        boolean result = gameSolverService.deleteSolutionById(999L);

        assertFalse(result);
        verify(solutionRepository).existsById(999L);
        verify(solutionRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteAllSolutions_shouldReturnNumberOfDeletedSolutions() {
        when(solutionRepository.count()).thenReturn(5L);
        doNothing().when(solutionRepository).deleteAll();

        long result = gameSolverService.deleteAllSolutions();

        assertEquals(5L, result);
        verify(solutionRepository).count();
        verify(solutionRepository).deleteAll();
    }

    @Test
    void updateSolution_whenSolutionExists_shouldUpdateAndReturnSolution() {
        List<Integer> newNumbers = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1);
        Solution updatedSolution = new Solution(1L, newNumbers);
        
        when(solutionRepository.findById(1L)).thenReturn(Optional.of(testSolution));
        when(solutionRepository.findAll()).thenReturn(Collections.singletonList(testSolution));
        when(solutionRepository.save(any(Solution.class))).thenReturn(updatedSolution);

        Optional<Solution> result = gameSolverService.updateSolution(1L, newNumbers);

        assertTrue(result.isPresent());
        assertEquals(updatedSolution, result.get());
        verify(solutionRepository).findById(1L);
        verify(solutionRepository).findAll();
        verify(solutionRepository).save(any(Solution.class));
    }

    @Test
    void updateSolution_whenSolutionDoesNotExist_shouldReturnEmpty() {
        when(solutionRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<Solution> result = gameSolverService.updateSolution(999L, testNumbers);

        assertTrue(result.isEmpty());
        verify(solutionRepository).findById(999L);
        verify(solutionRepository, never()).findAll();
        verify(solutionRepository, never()).save(any(Solution.class));
    }

    @Test
    void updateSolution_whenDuplicateExists_shouldDeleteDuplicateAndUpdateSolution() {
        List<Integer> newNumbers = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1);
        Solution duplicateSolution = new Solution(2L, newNumbers);
        Solution updatedSolution = new Solution(1L, newNumbers);
        
        when(solutionRepository.findById(1L)).thenReturn(Optional.of(testSolution));
        when(solutionRepository.findAll()).thenReturn(Arrays.asList(testSolution, duplicateSolution));
        when(solutionRepository.save(any(Solution.class))).thenReturn(updatedSolution);
        doNothing().when(solutionRepository).deleteById(2L);

        Optional<Solution> result = gameSolverService.updateSolution(1L, newNumbers);

        assertTrue(result.isPresent());
        assertEquals(updatedSolution, result.get());
        verify(solutionRepository).findById(1L);
        verify(solutionRepository).findAll();
        verify(solutionRepository).deleteById(2L);
        verify(solutionRepository).save(any(Solution.class));
    }
}
