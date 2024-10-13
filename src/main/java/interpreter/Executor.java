package interpreter;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.parser.ParseException;
import functions.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

        // For value functions
        functions.put("val", "val"); // Add val function

        // For input functions
        functions.put("input", "input");
        functions.put("read", "input");
        functions.put("get", "input");
        functions.put("ask", "input");
        functions.put("receive", "input");

        // For click functions
        functions.put("click", "click");
        functions.put("tap", "click");

        // For middleClick functions
        functions.put("middleclick", "middleClick");
        functions.put("middleClick", "middleClick");

        // For double-click functions
        functions.put("doubleclick", "doubleClick");
        functions.put("doubletap", "doubleClick");
        functions.put("doubleClick", "doubleClick");
        functions.put("doubleTap", "doubleClick");
        functions.put("dblClick", "doubleClick");
        functions.put("dblTap", "doubleClick");
        functions.put("dblclick", "doubleClick");
        functions.put("dbltap", "doubleClick");

        // for right click functions
        functions.put("rightclick", "rightClick");
        functions.put("rightClick", "rightClick");
        
        // for drag functions
        functions.put("drag", "drag");

        // for scroll functions
        functions.put("scroll", "scroll");

        // For cursor functions
        functions.put("cursor", "cursor");
        functions.put("hover", "cursor");

        // for screen width function
        functions.put("scrwidth", "scrwidth");
        functions.put("screenwidth", "scrwidth");
        functions.put("screenWidth", "scrwidth");
        functions.put("scrWidth", "scrwidth");
        
        // for screen height function
        functions.put("scrheight", "scrheight");
        functions.put("screenheight", "scrheight");
        functions.put("screenHeight", "scrheight");
        functions.put("scrHeight", "scrheight");

        // for type functions
        functions.put("type", "type");
        functions.put("write", "type");
        functions.put("enter", "type");

        // for wait functions
        functions.put("wait", "waitFor");
        functions.put("sleep", "waitFor");
        functions.put("pause", "waitFor");
        functions.put("delay", "waitFor");
        functions.put("hold", "waitFor");

        // for pressKeys functions
        functions.put("pressKeys", "pressKeys");
        functions.put("presskeys", "pressKeys");
        functions.put("press", "pressKeys");

        // for open app
        functions.put("open", "openApp");
        functions.put("launch", "openApp");
        functions.put("start", "openApp");

        // for close app
        functions.put("closeApp", "closeApp");
        functions.put("closeapp", "closeApp");
        functions.put("kill", "closeApp");
        functions.put("close", "closeApp");

        // for getClipboard
        functions.put("getClipboard", "getClipboard");
        functions.put("getclipboard", "getClipboard");

        // for setClipboard
        functions.put("setClipboard", "setClipboard");
        functions.put("setclipboard", "setClipboard");
        functions.put("clipboard", "setClipboard");

        // for create
        functions.put("create", "create");

        // exists
        functions.put("exists", "tableExists");

        // for insert
        functions.put("insert", "insert");
        
    }

    private boolean isScientificNotation(String value) {
        String regex = "^[+-]?\\d*\\.?\\d+[eE][+-]?\\d+$";
        return value.matches(regex);
    }

    private String evaluateExpression(String expression) {
        String originalExpression = expression;
    
        // Use regex to split expression into tokens, capturing quoted strings
        // This regex will handle strings enclosed in single or double quotes, including multiline.
        String[] tokens = expression.split("(?<!\\\\)\\s+(?=(?:[^\\\"]*\\\"[^\\\"]*\\\"*[^\\\"]*$)*[^\\\"]*$)");
    
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i].trim();
    
            // If the token is a variable, retrieve its value from memoryManager
            if (memoryManager.exists(token)) {
                Object value = memoryManager.get(token);
                String valueStr = value instanceof Number ? String.valueOf(((Number) value).intValue()) : value.toString();
                expression = expression.replace(token, valueStr);
            } 
            // If the token is a function, evaluate the function
            else if (functions.containsKey(token)) {
                List<String> args = new ArrayList<>();
    
                // From the next token onwards, treat them as function arguments
                for (int j = i + 1; j < tokens.length; j++) {
                    args.add(tokens[j].trim());
                }
    
                // Evaluate the function with the arguments
                Object response = evaluateFunction(token, args);
                String result = response instanceof Number ? String.valueOf(((Number) response).intValue()) : response.toString();
    
                // Replace the function and its arguments in the expression
                String argsString = String.join(" ", args);
                if (args.isEmpty()) {
                    expression = expression.replace(token, result);
                } else {
                    expression = expression.replace(token + " " + argsString, result);
                }
                break; // Exit after replacing the function call to avoid reprocessing
            }
        }
    
        // If the expression is empty or null, return the original expression
        if (expression == null || expression.isEmpty()) {
            return originalExpression;
        }
    
        // Evaluate the final expression (if numeric operations are required)
        Expression expr = new Expression(expression);
        EvaluationValue result;
        try {
            result = expr.evaluate();
            String numberValue = result.getNumberValue().toString();
    
            // Convert numberValue to BigDecimal for rounding
            BigDecimal bdValue = new BigDecimal(numberValue);
            bdValue = bdValue.setScale(2, RoundingMode.HALF_UP); // Round to 2 decimal places
    
            return bdValue.toString(); // Return the rounded value
        } catch (EvaluationException | ParseException e) {
            return expression; // Return original expression if evaluation fails
        }
    }
                  
    private Object evaluateFunction(String functionName, List<String> args) {
    // Evaluate each argument before passing it to the function
    for (int i = 0; i < args.size(); i++) {
        String arg = args.get(i);
        // If the argument is a variable, evaluate it
        if (memoryManager.exists(arg)) {
            args.set(i, memoryManager.get(arg).toString());
        } else {
            // Try to evaluate it as an expression if it's not a known variable
            try {
                args.set(i, evaluateExpression(arg));
            } catch (Exception e) {
                // Keep the original if evaluation fails
                args.set(i, arg);
            }
        }
    }
    
        if ("print".equals(functions.get(functionName))) { 
            fn.print(args); 
        } else if ("val".equals(functions.get(functionName))) {
            return fn.val(args);
        } else if ("input".equals(functions.get(functionName))) { 
            fn.input(args); 
        } else if ("click".equals(functions.get(functionName))) { 
            fn.click(args); 
        } else if ("cursor".equals(functions.get(functionName))) { 
            fn.cursor(args); 
        } else if ("doubleClick".equals(functions.get(functionName))) { 
            fn.doubleClick(args); 
        } else if ("scrwidth".equals(functions.get(functionName))) { 
            return fn.scrwidth(args);
        } else if ("scrheight".equals(functions.get(functionName))) { 
            return fn.scrheight(args);
        } else if ("type".equals(functions.get(functionName))) {
            fn.type(args);
        } else if ("waitFor".equals(functions.get(functionName))) {
            fn.waitFor(args);
        } else if ("middleClick".equals(functions.get(functionName))) {
            fn.middleClick();
        } else if ("rightClick".equals(functions.get(functionName))) {
            fn.rightClick(args);
        } else if ("drag".equals(functions.get(functionName))) {
            fn.drag(args);
        } else if ("scroll".equals(functions.get(functionName))) {
            fn.scroll(args);
        } else if ("pressKeys".equals(functions.get(functionName))) {
            fn.pressKeys(args);
        } else if ("openApp".equals(functions.get(functionName))) {
            fn.openApp(args);
        } else if ("closeApp".equals(functions.get(functionName))) {
            fn.closeApp(args);
        } else if ("getClipboard".equals(functions.get(functionName))) {
            return fn.getClipboard();
        } else if ("setClipboard".equals(functions.get(functionName))) {
            fn.setClipboard(args);
        } else if ("create".equals(functions.get(functionName))) {
            fn.create(args);
        } else if ("tableExists".equals(functions.get(functionName))) {
            return fn.tableExists(args);
        } else if ("insert".equals(functions.get(functionName))) {
            fn.insert(args);
        } else {
            return functionName; // Return the function name if not found
        }
        return "";
    }
    
    public void execute() {
        while (index < tokens.size()) {
            String token = tokens.get(index);

        // Modify the assignment section to handle the evaluated function value
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

            // Correct floating-point to integer if necessary
            if (isScientificNotation(evaluatedValue)) {  // If scientific notation exists, convert it
                try {
                    double parsedValue = Double.parseDouble(evaluatedValue);
                    evaluatedValue = String.valueOf((int) parsedValue); // Convert to integer
                } catch (NumberFormatException e) {
                    log.log("Error parsing value: " + evaluatedValue, "error");
                }
            }

            memoryManager.assign(variable, evaluatedValue); // Assign evaluated value to variable
            continue; // Skip further processing for this token
        }
        // Handle "if" condition
        if (token.equals("if")) {
                String cond = "";
                List<String> ifBlock = new ArrayList<>();
                List<String> elseBlock = new ArrayList<>();
                
                index++;
                while (index < tokens.size() && !tokens.get(index).equals("\\n")) {
                    cond += tokens.get(index) + " ";
                    index++;
                }

                //split with spaces and evaluate the expression
                for (String s : cond.split(" ")) {
                    if (memoryManager.exists(s)) {
                        cond = cond.replace(s, memoryManager.get(s).toString());
                    }
                }

                index++; // Move past the newline after the condition

                // Collect the if block
                while (index < tokens.size() && !tokens.get(index).equals("else") && !tokens.get(index).equals("endif")) {
                    ifBlock.add(tokens.get(index));
                    index++;
                }

                // Check for the else keyword
                if (index < tokens.size() && tokens.get(index).equals("else")) {
                    index++; // Move past "else"

                    // Collect the else block
                    while (index < tokens.size() && !tokens.get(index).equals("endif")) {
                        elseBlock.add(tokens.get(index));
                        index++;
                    }
                }

                // Check for "endif"
                if (index < tokens.size() && tokens.get(index).equals("endif")) {
                    index++; // Move past "endif"
                }

                // Evaluate the condition and execute the appropriate block
                if (evaluvate.evaluate(cond.trim())) {
                    // Pass the same MemoryManager to the Executor
                    Executor ifBlockExecutor = new Executor(ifBlock, memoryManager);
                    ifBlockExecutor.execute();
                } else {
                    // Execute the else block if the condition is false
                    if (!elseBlock.isEmpty()) {
                        Executor elseBlockExecutor = new Executor(elseBlock, memoryManager);
                        elseBlockExecutor.execute();
                    }
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
            evaluateFunction(token, args);
        } else if (token.equals("\\n")) {
            index++;
        } else { 
                log.log("Unknown token: " + token + " at pos " + index, "error");
                index++;
            }
        }
    }
}
