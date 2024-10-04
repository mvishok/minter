package interpreter;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
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

    public Executor(List<String> tokens, MemoryManager memoryManager) {
        this.tokens = tokens;
        this.index = 0;
        this.functions = new HashMap<>();
        this.memoryManager = memoryManager;
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

        // For input functions
        functions.put("input", "input");
        functions.put("read", "input");
        functions.put("get", "input");
        functions.put("ask", "input");
        functions.put("receive", "input");
    }

    // Function to evaluate the expression
    private String evaluateExpression(String expression) {
        // Replace variable names in the expression with their values
        for (String token : expression.split(" ")) {
            if (memoryManager.exists(token)) {
                Object value = memoryManager.get(token);
                expression = expression.replace(token, (value != null ? value.toString() : "0")); // Default to 0 if null
            }
        }

        try {
            if (expression == null || expression.isEmpty()) {
                throw new IllegalArgumentException("Expression is null or empty");
            }

            log.log("Evaluating expression: " + expression, "info");
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
            Object result = engine.eval(expression);
            
            return String.valueOf(result);
        } catch (ScriptException e) {
            log.log("Error evaluating expression: " + expression + " | " + e.getMessage(), "error");
            return expression; // Return the original expression if evaluation fails
        }
    }


    public void execute() {
        while (index < tokens.size()) {
            String token = tokens.get(index);

            // Handle variable assignment
            if (token.matches("[a-zA-Z_][a-zA-Z0-9_]*") && index + 1 < tokens.size() && tokens.get(index + 1).equals("=")) {
                String variable = token; // The identifier
                index += 2; // Move past the identifier and '='

                // Get the value after the assignment
                StringBuilder valueBuilder = new StringBuilder();
                while (index < tokens.size() && !tokens.get(index).equals("\\n")) {
                    valueBuilder.append(tokens.get(index)).append(" ");
                    index++;
                }
                String value = valueBuilder.toString().trim(); // Trim any extra spaces

                // Evaluate the expression
                String evaluatedValue = evaluateExpression(value);
                memoryManager.assign(variable, evaluatedValue); // Assign evaluated value to variable

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

                // Check the condition before executing the block
                if (evaluvate.evaluate(cond.trim())) {
                    // Pass the same MemoryManager to the Executor
                    Executor blockExecutor = new Executor(block, memoryManager);
                    blockExecutor.execute();
                }
            } else if (functions.containsKey(token)) {
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
                } else if ("input".equals(functions.get(token))) { 
                    fn.input(args); 
                }
            } else if (token.equals("\\n")) {
                index++;
            } else { 
                log.log("Unknown token: " + token + " at pos " + index, "error");
                index++;
            }
        }
    }
}
