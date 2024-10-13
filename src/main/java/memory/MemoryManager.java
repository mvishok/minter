package memory;

import java.util.HashMap;
import java.util.Map;

public class MemoryManager {

    private Map<String, String> variables;

    public MemoryManager() {
        variables = new HashMap<>();
    }

    // Assign a value to a variable
    public void assign(String variable, String value) {
        variables.put(variable, value);
    }

    // Retrieve a variable's value
    public String get(String variable) {
        return variables.get(variable);
    }

    // Check if a variable exists
    public boolean exists(String variable) {
        return variables.containsKey(variable);
    }
}
