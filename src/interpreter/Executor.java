package interpreter;

import functions.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import memory.MemoryManager;
import modules.*;

public class Executor {

    private List<String> tokens;
    private int index;
    private Map<String, String> functions;
    private MemoryManager memoryManager;  // Memory manager instance
    private fns fn; // Reference to functions

    logger log = new logger();
    Conditions evaluvate;

    public Executor(List<String> tokens) {
        this.tokens = tokens;
        this.index = 0;
        this.functions = new HashMap<>();
        this.memoryManager = new MemoryManager();  // Initialize memory manager
        this.fn = new fns(memoryManager); // Pass MemoryManager to fns
        this.evaluvate = new Conditions(memoryManager); // Pass MemoryManager to Conditions

        // For print functions
        functions.put("print", "print");
        functions.put("display", "print");
        functions.put("show", "print");
        functions.put("say", "print");
        functions.put("output", "print");
        functions.put("write", "print");
        functions.put("val", "val"); // Add val function
    }

    public void execute() {
        while (index < tokens.size()) {
            String token = tokens.get(index);

            // Handle variable assignment
            if (token.matches("[a-zA-Z_][a-zA-Z0-9_]*") && index + 1 < tokens.size() && tokens.get(index + 1).equals("=")) {
                String variable = token; // The identifier
                index += 2; // Move past the identifier and '='

                // Get the value after the assignment
                String value = "";
                while (index < tokens.size() && !tokens.get(index).equals("\\n")) {
                    value += tokens.get(index) + " ";
                    index++;
                }
                value = value.trim(); // Trim any extra spaces

                // If the value is a variable, retrieve its value
                if (memoryManager.exists(value)) {
                    value = memoryManager.get(value);
                }

                memoryManager.assign(variable, value);  // Assign value to variable
                continue; // Skip further processing for this token
            }


            // Handle "if" condition
            if (token.equals("if")) {
                String cond = "";
                List<String> block = new ArrayList<>();

                index++;
                while (index < tokens.size() && !tokens.get(index).equals("\\n")) {
                    cond += tokens.get(index) + " ";
                    index++;
                }

                index++; // Move past the newline after the condition

                while (index < tokens.size() && !tokens.get(index).equals("endif")) {
                    block.add(tokens.get(index));
                    index++;
                }

                if (index < tokens.size() && tokens.get(index).equals("endif")) {
                    index++; // Move past "endif"
                }

                if (evaluvate.evaluate(cond.trim())) {
                    Executor blockExecutor = new Executor(block);
                    blockExecutor.execute();
                }
            } 
            
            else if (functions.containsKey(token)) {
                List<String> args = new ArrayList<>();
                index++; // Move past the function name

                // Check for parentheses for function arguments
                if (index < tokens.size() && tokens.get(index).equals("(")) {
                    index++; // Skip the '('
                    while (index < tokens.size() && !tokens.get(index).equals(")")) {
                        if (tokens.get(index).equals(",")) {
                            index++; // Skip commas
                            continue;
                        }
                        args.add(tokens.get(index));
                        index++;
                    }
                    index++; // Move past ')'
                } else {
                    // Otherwise, just read until newline
                    while (index < tokens.size() && !tokens.get(index).equals("\\n")) {
                        args.add(tokens.get(index));
                        index++;
                    }
                }

                // Call the appropriate function
                if ("print".equals(functions.get(token))) { 
                    fn.print(args); 
                } else if ("val".equals(functions.get(token))) { 
                    String result = fn.val(args);
                    System.out.println(result); 
                }
            } 


            // Handle new lines
            else if (token.equals("\\n")) {
                index++;
            } 
            else { 
                log.log("Unknown token: " + token + " at pos " + index, "error");
                index++;
            }
        }
    }
}