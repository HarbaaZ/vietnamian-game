package com.example.game_back.solution.controllers;

import com.example.game_back.solution.services.GameSolverService;
import com.example.game_back.solution.models.Solution;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/game/solutions")
public class GameController {

    private final GameSolverService gameSolverService;

    public GameController(GameSolverService gameSolverService) {
        this.gameSolverService = gameSolverService;
    }

    /**
     * Endpoint to generate all possible solutions and save it to the database.
     * This method will clear the database before generating new solutions.
     *
     * @return A map containing the calculation time and total number of solutions
     */
    @GetMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateSolutions() {
        long calculationTime = gameSolverService.generateSolutionsEfficient();
        List<Solution> solutions = gameSolverService.getAllSolutions();

        Map<String, Object> response = new HashMap<>();
        response.put("calculationTimeMs", calculationTime);
        response.put("totalSolutions", solutions.size());

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to get all solutions from the database.
     *
     * @return A list of all solutions
     */
    @GetMapping()
    public ResponseEntity<List<Solution>> getAllSolutions() {
        return ResponseEntity.ok(gameSolverService.getAllSolutions());
    }

    /**
     * Endpoint to propose a solution to the problem.
     * The solution will be saved to the database and validated.
     * 
     * @param numbers List of 9 numbers representing the proposed solution
     * @return The saved solution with validation status
     */
    @PostMapping()
    public ResponseEntity<Solution> createSolution(@RequestBody List<Integer> numbers) {
        if (numbers == null || numbers.size() != 9) {
            return ResponseEntity.badRequest().build();
        }
        
        Solution savedSolution = gameSolverService.createSolution(numbers);
        return ResponseEntity.ok(savedSolution);
    }

    /**
     * Endpoint to get a solution by its ID.
     *
     * @param id The ID of the solution to retrieve
     * @return The solution if found, or 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Solution> getSolutionById(@PathVariable Long id) {
        Optional<Solution> solution = gameSolverService.getSolutionById(id);
        
        return solution
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Endpoint to delete a solution by its ID.
     *
     * @param id The ID of the solution to delete
     * @return 204 No Content if deleted successfully, 404 Not Found if not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSolutionById(@PathVariable Long id) {
        boolean deleted = gameSolverService.deleteSolutionById(id);
        
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint to delete all solutions from the database.
     *
     * @return A response containing the number of solutions deleted
     */
    @DeleteMapping()
    public ResponseEntity<Map<String, Object>> deleteAllSolutions() {
        long deletedCount = gameSolverService.deleteAllSolutions();
        
        Map<String, Object> response = new HashMap<>();
        response.put("deletedCount", deletedCount);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to update a solution by its ID.
     * If another solution with the same numbers already exists, that solution will be deleted
     * to avoid duplicates.
     *
     * @param id The ID of the solution to update
     * @param numbers The new list of numbers for the solution
     * @return The updated solution if found, or 404 Not Found if not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<Solution> updateSolution(@PathVariable Long id, @RequestBody List<Integer> numbers) {
        if (numbers == null || numbers.size() != 9) {
            return ResponseEntity.badRequest().build();
        }
        
        Optional<Solution> updatedSolution = gameSolverService.updateSolution(id, numbers);
        
        return updatedSolution
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
