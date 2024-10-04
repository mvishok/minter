package interpreter;

import memory.MemoryManager;

public class Conditions {

    private MemoryManager memoryManager; // Reference to MemoryManager

    public Conditions(MemoryManager memoryManager) {
        this.memoryManager = memoryManager; // Initialize MemoryManager
    }

    public boolean evaluate(String condition) {
        // Split the condition into parts: "1", "==", "1"
        String[] parts = condition.trim().split(" ");

        if (parts.length != 3) {
            // Invalid condition format
            System.out.println("Invalid condition: " + condition);
            return false;
        }

        String left = parts[0].trim();
        String operator = parts[1].trim();
        String right = parts[2].trim();

        // Retrieve values from MemoryManager if they are variables
        if (memoryManager.exists(left)) {
            left = memoryManager.get(left); // Get the value of the variable
        }
        
        if (memoryManager.exists(right)) {
            right = memoryManager.get(right); // Get the value of the variable
        }

        // Basic comparison logic
        try {
            int leftValue = Integer.parseInt(left);
            int rightValue = Integer.parseInt(right);

            switch (operator) {
                case "==" -> {
                    return leftValue == rightValue;
                }
                case "!=" -> {
                    return leftValue != rightValue;
                }
                case "<" -> {
                    return leftValue < rightValue;
                }
                case ">" -> {
                    return leftValue > rightValue;
                }
                case "<=" -> {
                    return leftValue <= rightValue;
                }
                case ">=" -> {
                    return leftValue >= rightValue;
                }
                default -> {
                    System.out.println("Unsupported operator: " + operator);
                    return false;
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format in condition: " + condition);
            return false;
        }
    }
}