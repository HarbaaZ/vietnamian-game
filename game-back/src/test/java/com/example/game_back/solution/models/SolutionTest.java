package com.example.game_back.solution.models;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SolutionTest {

    @Test
    void testConstructor() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Solution solution = new Solution(1L, numbers);
        
        assertEquals(1L, solution.getId());
        assertEquals(numbers, solution.getNumbers());
        assertEquals(solution.isValid(), solution.isCorrect());
    }
    
    @Test
    void testInvalidSolution() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Solution solution = new Solution(null, numbers);
        
        assertFalse(solution.isValid());
        assertFalse(solution.isCorrect());
    }
    
    @Test
    void testInvalidSolutionWithWrongSize() {
        List<Integer> tooFewNumbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);
        Solution solution = new Solution(null, tooFewNumbers);
        
        assertFalse(solution.isValid());
        assertFalse(solution.isCorrect());
    }
}
