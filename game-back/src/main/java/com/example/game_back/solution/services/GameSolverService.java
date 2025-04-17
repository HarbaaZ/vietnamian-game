package com.example.game_back.solution.services;

import com.example.game_back.solution.models.Solution;
import com.example.game_back.solution.repositories.SolutionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class GameSolverService {

    private final SolutionRepository solutionRepository;

    public GameSolverService(SolutionRepository solutionRepository) {
        this.solutionRepository = solutionRepository;
    }

    public long generateSolutionsEfficient() {
        solutionRepository.deleteAll();
        List<Solution> validSolutions = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        int[] numbers = new int[9];
        boolean[] used = new boolean[10];

        findSolutions(0, numbers, used, validSolutions);

        solutionRepository.saveAll(validSolutions);

        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }

    private void findSolutions(int position, int[] numbers, boolean[] used, List<Solution> validSolutions) {
        if (position == 9) {
            List<Integer> numbersList = new ArrayList<>();
            for (int num : numbers) {
                numbersList.add(num);
            }

            Solution solution = new Solution(null, numbersList);
            if (solution.isValid()) {
                validSolutions.add(solution);
            }
            return;
        }

        for (int num = 1; num <= 9; num++) {
            if (!used[num]) {
                used[num] = true;
                numbers[position] = num;

                findSolutions(position + 1, numbers, used, validSolutions);

                used[num] = false;
            }
        }
    }

    public List<Solution> getAllSolutions() {
        return solutionRepository.findAll();
    }

    /**
     * Saves a user-proposed solution to the database if it doesn't already exist.
     * The solution will be validated and the 'correct' flag will be set accordingly.
     * 
     * @param numbers A list of 9 numbers representing the proposed solution
     * @return The saved solution with validation status, or the existing solution if already in database
     */
    public Solution createSolution(List<Integer> numbers) {
        List<Solution> allSolutions = solutionRepository.findAll();
        
        Optional<Solution> existingSolution = allSolutions.stream()
                .filter(sol -> sol.getNumbers().equals(numbers))
                .findFirst();
        
        if (existingSolution.isPresent()) {
            return existingSolution.get();
        }
        
        Solution solution = new Solution(null, numbers);
        return solutionRepository.save(solution);
    }

    /**
     * Retrieves a solution by its ID.
     * 
     * @param id The ID of the solution to retrieve
     * @return The solution if found, or empty if not found
     */
    public Optional<Solution> getSolutionById(Long id) {
        return solutionRepository.findById(id);
    }

    /**
     * Deletes a solution by its ID.
     * 
     * @param id The ID of the solution to delete
     * @return true if the solution was found and deleted, false if not found
     */
    public boolean deleteSolutionById(Long id) {
        if (solutionRepository.existsById(id)) {
            solutionRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Deletes all solutions from the database.
     * 
     * @return The number of solutions deleted
     */
    public long deleteAllSolutions() {
        long count = solutionRepository.count();
        solutionRepository.deleteAll();
        return count;
    }

    /**
     * Updates a solution by its ID with new numbers.
     * If another solution with the same numbers already exists, that solution will be deleted
     * to avoid duplicates.
     * 
     * @param id The ID of the solution to update
     * @param newNumbers The new list of numbers for the solution
     * @return The updated solution if found, or empty if not found
     */
    public Optional<Solution> updateSolution(Long id, List<Integer> newNumbers) {
        Optional<Solution> solutionToUpdate = solutionRepository.findById(id);
        
        if (solutionToUpdate.isEmpty()) {
            return Optional.empty();
        }
        
        List<Solution> allSolutions = solutionRepository.findAll();
        
        Optional<Solution> duplicateSolution = allSolutions.stream()
                .filter(sol -> !sol.getId().equals(id))
                .filter(sol -> sol.getNumbers().equals(newNumbers))
                .findFirst();
        
        duplicateSolution.ifPresent(solution -> solutionRepository.deleteById(solution.getId()));
        
        Solution solution = solutionToUpdate.get();
        solution.setNumbers(newNumbers);
        solution.setCorrect(solution.isValid());
        
        return Optional.of(solutionRepository.save(solution));
    }
}
