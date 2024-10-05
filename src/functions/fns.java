package functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import memory.MemoryManager;
import java.lang.reflect.Method;
import modules.logger;
import java.awt.Robot;  // Import Robot for automation
import java.awt.AWTException;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.Toolkit; // Import Toolkit for screen size
import java.awt.Dimension; // Import Dimension for screen size

public class fns {

    private logger log = new logger(); // Logger instance

    private MemoryManager memoryManager; // Reference to MemoryManager

    private Scanner scanner = new Scanner(System.in); // Scanner for user input

    private Robot robot; // Robot instance for automation tasks

    public fns(MemoryManager memoryManager) {
        this.memoryManager = memoryManager; // Initialize MemoryManager
        try {
            this.robot = new Robot(); // Initialize the Robot instance
        } catch (AWTException e) {
            e.printStackTrace();
        }
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
                    // Handle the error for invalid numbers
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
        if (!args.isEmpty() && args.get(0).equals("val")) {
            List<String> valArgs = args.subList(1, args.size()); // Get all args after 'val'
            String valueOutput = val(valArgs).toString(); // Get the value
            System.out.println(valueOutput); // Print the values returned by val
        } else {
            List<Object> pargs = parse(args);
            for (Object arg : pargs) {
                System.out.print(arg + " ");
            }
            System.out.println();
        }
    }

    public Object val(List<String> args) {
        if (args.isEmpty()) {
            return ""; // If no arguments are provided, return an empty string or default value
        }
    
        String firstArg = args.get(0); // Get the first argument, which could be a variable or function name
        List<String> remainingArgs = args.subList(1, args.size()); // All other arguments are considered function parameters
    
        // Check if the first argument is a function that needs to be executed
        try {
            Method method = this.getClass().getDeclaredMethod(firstArg, List.class); // Look for the function by name
            Object result = method.invoke(this, remainingArgs); // Call the function with the remaining arguments
            return result; // Return the result (it could be String, Integer, etc.)
        } catch (NoSuchMethodException e) {
            // If it's not a function, check if it's a variable stored in memory
            if (memoryManager.exists(firstArg)) {
                return memoryManager.get(firstArg); // Return the value of the variable from memory
            } else {
                return firstArg; // Return the argument as is if it's neither a function nor a variable
            }
        } catch (Exception e) {
            e.printStackTrace(); // Handle other exceptions
            return ""; // Return an empty string or handle the error as needed
        }
    }
    
    
    public void input(List<String> args) {
        if (args.isEmpty()) {
            return;
        }

        String varName = args.get(0); // Get the variable name from the first argument
        String userInput = scanner.nextLine(); // Read user input

        // Store the input in MemoryManager
        memoryManager.assign(varName, userInput); // Assign the input value to the variable
    }

    public String executeFunction(String funcName, List<String> params) {
        try {
            // Use reflection to find the method
            Method method = this.getClass().getDeclaredMethod(funcName, List.class);
            
            // Invoke the method dynamically and capture the return value
            String result = (String) method.invoke(this, (Object) params);
            
            return result; // Return the result of the function call
        } catch (NoSuchMethodException e) {
            log.log("Function " + funcName + " does not exist.", "error");
            return ""; // Return an empty string or handle the error as needed
        } catch (Exception e) {
            e.printStackTrace(); // Handle other exceptions
            return ""; // Return an empty string or handle the error as needed
        }
    }

    // Function to move the cursor to a specific (x, y) position
    public void cursor(List<String> args) {
        if (args.size() == 2) {
            try {
                double xDouble = Double.parseDouble(args.get(0)); // Parse as double
                double yDouble = Double.parseDouble(args.get(1)); // Parse as double
            
                // Round or convert to int (choose rounding strategy based on your needs)
                int x = (int) Math.round(xDouble); // Rounding to nearest integer
                int y = (int) Math.round(yDouble); // Rounding to nearest integer
            
                robot.mouseMove(x, y); // Move the mouse to (x, y)
            } catch (NumberFormatException e) {
                log.log("Invalid coordinates: " + args, "error");
            }
            
        } else {
            log.log("Invalid arguments for cursor. Expected 2 arguments.", "error");
        }
    }

    // Function to simulate a mouse click at the current cursor position
    public void click(List<String> args) {
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK); // Simulate mouse press
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK); // Simulate mouse release
    }

    // Function to simulate a double mouse click
    public void doubleClick(List<String> args) {
        click(new ArrayList<>()); // Perform the first click
        click(new ArrayList<>()); // Perform the second click
    }

    // Function to simulate a right mouse click
    public void rightClick(List<String> args) {
        robot.mousePress(InputEvent.BUTTON3_DOWN_MASK); // Simulate right mouse press
        robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK); // Simulate right mouse release
    }

    // Function to simulate dragging from (x1, y1) to (x2, y2)
    public void drag(List<String> args) {
        if (args.size() == 4) {
            try {
                int x1 = Integer.parseInt(args.get(0));
                int y1 = Integer.parseInt(args.get(1));
                int x2 = Integer.parseInt(args.get(2));
                int y2 = Integer.parseInt(args.get(3));

                robot.mouseMove(x1, y1); // Move to starting position
                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK); // Press mouse button
                robot.mouseMove(x2, y2); // Drag to the end position
                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK); // Release mouse button
                
            } catch (NumberFormatException e) {
                log.log("Invalid coordinates for drag: " + args, "error");
            }
        } else {
            log.log("Invalid arguments for drag. Expected 4 arguments.", "error");
        }
    }

    // Function to simulate mouse scrolling
    public void scroll(List<String> args) {
        if (args.size() == 1) {
            String direction = args.get(0).toLowerCase();
            if ("up".equals(direction)) {
                robot.mouseWheel(-1); // Scroll up
            } else if ("down".equals(direction)) {
                robot.mouseWheel(1); // Scroll down
            } else {
                log.log("Invalid argument for scroll. Use 'up' or 'down'.", "error");
            }
        } else {
            log.log("Invalid arguments for scroll. Expected 1 argument.", "error");
        }
    }

    // Function to return screen width
    public String scrwidth(List<String> args) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) screenSize.getWidth();
        return String.valueOf(width); // Return screen width as string
    }

    // Function to return screen height
    public String scrheight(List<String> args) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = (int) screenSize.getHeight();
        return String.valueOf(height); // Return screen height as string
    }

    // Function to wait for a specified amount of milliseconds
    public void waitFor(List<String> args) {
        if (args.size() == 1) {
            try {
                double ms = Double.parseDouble(args.get(0)); // Parse as double
                int milliseconds = (int) Math.round(ms); // Convert to integer
                Thread.sleep(milliseconds); // Pause execution for the specified duration
            } catch (NumberFormatException e) {
                log.log("Invalid argument for wait. Must be a number: " + args, "error");
            } catch (InterruptedException e) {
                log.log("Wait interrupted: " + e.getMessage(), "error");
            }
        } else {
            log.log("Invalid arguments for wait. Expected 1 argument.", "error");
        }
    }

    // Function to type the arguments as keyboard input
    public void type(List<String> args) {
        for (String arg : args) {
            for (char c : arg.toCharArray()) {
                robot.keyPress(KeyEvent.getExtendedKeyCodeForChar(c)); // Simulate key press
                robot.keyRelease(KeyEvent.getExtendedKeyCodeForChar(c)); // Simulate key release
                try {
                    Thread.sleep(50); // Optional: wait for a short duration between key presses
                } catch (InterruptedException e) {
                    log.log("Typing interrupted: " + e.getMessage(), "error");
                }
            }
            // Simulate pressing the space key after each argument
            robot.keyPress(KeyEvent.VK_SPACE);
            robot.keyRelease(KeyEvent.VK_SPACE);
        }
    }

}
