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

    logger log = new logger();
    fns fn = new fns();
    Conditions evaluvate = new Conditions();

    public Executor(List<String> tokens) {
        this.tokens = tokens;
        this.index = 0;
        this.functions = new HashMap<>();
        this.memoryManager = new MemoryManager();  // Initialize memory manager

        // For print functions
        functions.put("print", "print");
        functions.put("display", "print");
        functions.put("show", "print");
        functions.put("say", "print");
        functions.put("output", "print");
        functions.put("write", "print");
    }

    public void execute() {
        while (index < tokens.size()) {
            String token = tokens.get(index);

            // Handle "if" condition
            if (token.equals("if")) {
                String cond = "";
                List<String> block = new ArrayList<>();

                index++;
                // Parse the condition part
                while (index < tokens.size() && !tokens.get(index).equals("\\n")) {
                    cond += tokens.get(index) + " ";
                    index++;
                }

                index++; // Move past the newline after the condition

                // Parse the block inside the "if" statement
                while (index < tokens.size() && !tokens.get(index).equals("endif")) {
                    block.add(tokens.get(index));
                    index++;
                }

                if (index < tokens.size() && tokens.get(index).equals("endif")) {
                    index++; // Move past "endif"
                }

                // If the condition evaluates to true, execute the block
                if (evaluvate.evaluate(cond)) {
                    Executor blockExecutor = new Executor(block);
                    blockExecutor.execute();
                }
            } 
            // Handle variable assignment
            else if (token.contains("=")) {
                String[] parts = token.split("=");
                if (parts.length == 2) {
                    String variable = parts[0].trim();
                    String value = parts[1].trim();
                    memoryManager.assign(variable, value);  // Assign value to variable
                } else {
                    log.log("Invalid assignment: " + token + " at pos " + index, "error");
                }
                index++;
            } 
            // Handle function calls like "print"
            else if (functions.containsKey(token) && index + 1 < tokens.size()) {
                if (functions.get(token).equals("print")) {
                    index++;

                    List<String> args = new ArrayList<>();
                    while (index < tokens.size() && !tokens.get(index).equals("\\n")) {
                        args.add(tokens.get(index));
                        index++;
                    }

                    if (index < tokens.size() && tokens.get(index).equals("\\n")) {
                        index++;
                    }

                    fn.print(args); // Call the print function with the arguments
                }
            } 
            // Handle new lines
            else if (token.equals("\\n")) {
                index++;
            } 
            // Log any unknown tokens
            else {
                log.log("Unknown token: " + token + " at pos " + index, "error");
                index++;
            }
        }
    }
}
