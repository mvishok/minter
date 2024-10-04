package functions;

import java.util.ArrayList;
import java.util.List;
import memory.MemoryManager;
import java.lang.reflect.Method;


public class fns {

    private MemoryManager memoryManager; // Reference to MemoryManager

    public fns(MemoryManager memoryManager) {
        this.memoryManager = memoryManager; // Initialize MemoryManager
    }

    private List<Object> parse(List<String> args) {
        List<Object> parsed = new ArrayList<>();
        for (String arg : args) {
            // If it is a literal, remove the quotes
            if (arg.startsWith("\"") && arg.endsWith("\"")) {
                parsed.add(arg.substring(1, arg.length() - 1));
            } else if (arg.startsWith("'") && arg.endsWith("'")) {
                parsed.add(arg.substring(1, arg.length() - 1));
            } else if (arg.equals("true") || arg.equals("false")) {
                parsed.add(Boolean.parseBoolean(arg));
            } else if (arg.contains(".")) {
                try {
                    parsed.add(Double.parseDouble(arg));
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number: " + arg);
                }
            } else {
                try {
                    parsed.add(Integer.parseInt(arg));
                } catch (NumberFormatException e) {
                    parsed.add(arg); // Keep as string if not a number
                }
            }
        }
        return parsed;
    }

    public void print(List<String> args) {
        // Check if the first argument is 'val'
        if (!args.isEmpty() && args.get(0).equals("val")) {
            List<String> valArgs = args.subList(1, args.size()); // Get all args after 'val'
            String valueOutput = val(valArgs); // Get the value
            System.out.println(valueOutput); // Print the values returned by val
        } else {
            List<Object> pargs = parse(args);
            for (Object arg : pargs) {
                System.out.print(arg + " ");
            }
            System.out.println();
        }
    }

    public String val(List<String> args) {
        StringBuilder result = new StringBuilder();
        
        // Handle val with provided arguments
        for (String arg : args) {
            if (memoryManager.exists(arg)) {
                result.append(memoryManager.get(arg)).append(" "); // Append value with space
            } else {
                result.append(arg).append(" "); // Append the argument itself if not found
            }
        }
        return result.toString().trim(); // Return trimmed result
    }

    public String executeFunction(String funcName, List<String> params) {
        try {
            // Use reflection to find the method
            Method method = this.getClass().getDeclaredMethod(funcName, List.class);
            
            // Invoke the method dynamically and capture the return value
            String result = (String) method.invoke(this, (Object) params);
            
            return result; // Return the result of the function call
        } catch (NoSuchMethodException e) {
            System.out.println("Function " + funcName + " does not exist.");
            return ""; // Return an empty string or handle the error as needed
        } catch (Exception e) {
            e.printStackTrace(); // Handle other exceptions
            return ""; // Return an empty string or handle the error as needed
        }
    }
}
