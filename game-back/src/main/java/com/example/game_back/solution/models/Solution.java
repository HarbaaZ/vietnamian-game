package com.example.game_back.solution.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OrderColumn;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Solution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    @OrderColumn
    private List<Integer> numbers;

    private boolean correct;

    public Solution(Long id, List<Integer> numbers) {
        this.id = id;
        this.numbers = numbers;
        this.correct = isValid();
    }

    @JsonIgnore
    public boolean isValid() {
        if (numbers.size() != 9) {
            return false;
        }

        int n1 = numbers.get(0);
        int n2 = numbers.get(1);
        int n3 = numbers.get(2);
        int n4 = numbers.get(3);
        int n5 = numbers.get(4);
        int n6 = numbers.get(5);
        int n7 = numbers.get(6);
        int n8 = numbers.get(7);
        int n9 = numbers.get(8);

        // BigDecimal is used to avoid precision issues with float
        BigDecimal result = BigDecimal.valueOf((n1 + 13.0 * n2 / n3 + n4 + 12 * n5 - n6 + (double)(n7 * n8) / n9));
        
        return result.equals(BigDecimal.valueOf(87.0));
    }
}
