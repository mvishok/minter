package interpreter;

import memory.MemoryManager;
import modules.logger;

public class Conditions {

    private logger log = new logger();

    private MemoryManager memoryManager; // Reference to MemoryManager

    public Conditions(MemoryManager memoryManager) {
        this.memoryManager = memoryManager; // Initialize MemoryManager
    }

    public boolean evaluate(String condition) {
        // Split the condition into parts: "1", "==", "1"
        String[] parts = condition.trim().split(" ");
    
        if (parts.length != 3) {
            // Invalid condition format
            log.log("Invalid condition: " + condition, "error");
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
            double leftValue = Double.parseDouble(left); // Change to Double
            double rightValue = Double.parseDouble(right); // Change to Double
    
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
                    log.log("Unsupported operator: " + operator, "error");
                    return false;
                }
            }
        } catch (NumberFormatException e) {
            log.log("Invalid number format in condition: " + condition, "error");
            return false;
        }
    }
    
}