package interpreter;

public class Conditions {

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

        // Basic comparison logic
        try {
            int leftValue = Integer.parseInt(left);
            int rightValue = Integer.parseInt(right);

            switch (operator) {
                case "==":
                    return leftValue == rightValue;
                case "!=":
                    return leftValue != rightValue;
                case "<":
                    return leftValue < rightValue;
                case ">":
                    return leftValue > rightValue;
                case "<=":
                    return leftValue <= rightValue;
                case ">=":
                    return leftValue >= rightValue;
                default:
                    System.out.println("Unsupported operator: " + operator);
                    return false;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format in condition: " + condition);
            return false;
        }
    }
}
