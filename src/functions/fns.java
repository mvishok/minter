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
import java.io.IOException;
import java.awt.Toolkit; // Import Toolkit for screen size
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
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
        
        // Check if the first argument is a function that needs to be executed
        try {
            Method method = this.getClass().getDeclaredMethod(firstArg); // Look for the function by name
            Object result = method.invoke(this); // Call the function with no parameters
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

    // Function to simulate pressing key combinations
    public void pressKeys(List<String> args) {
        // Create a map of string representations to KeyEvent constants
        java.util.Map<String, Integer> keyMap = new java.util.HashMap<>();
        keyMap.put("ctrl", KeyEvent.VK_CONTROL);
        keyMap.put("shift", KeyEvent.VK_SHIFT);
        keyMap.put("alt", KeyEvent.VK_ALT);
        keyMap.put("a", KeyEvent.VK_A);
        keyMap.put("b", KeyEvent.VK_B);
        keyMap.put("c", KeyEvent.VK_C);
        keyMap.put("d", KeyEvent.VK_D);
        keyMap.put("e", KeyEvent.VK_E);
        keyMap.put("f", KeyEvent.VK_F);
        keyMap.put("g", KeyEvent.VK_G);
        keyMap.put("h", KeyEvent.VK_H);
        keyMap.put("i", KeyEvent.VK_I);
        keyMap.put("j", KeyEvent.VK_J);
        keyMap.put("k", KeyEvent.VK_K);
        keyMap.put("l", KeyEvent.VK_L);
        keyMap.put("m", KeyEvent.VK_M);
        keyMap.put("n", KeyEvent.VK_N);
        keyMap.put("o", KeyEvent.VK_O);
        keyMap.put("p", KeyEvent.VK_P);
        keyMap.put("q", KeyEvent.VK_Q);
        keyMap.put("r", KeyEvent.VK_R);
        keyMap.put("s", KeyEvent.VK_S);
        keyMap.put("t", KeyEvent.VK_T);
        keyMap.put("u", KeyEvent.VK_U);
        keyMap.put("v", KeyEvent.VK_V);
        keyMap.put("w", KeyEvent.VK_W);
        keyMap.put("x", KeyEvent.VK_X);
        keyMap.put("y", KeyEvent.VK_Y);
        keyMap.put("z", KeyEvent.VK_Z);
        keyMap.put("0", KeyEvent.VK_0);
        keyMap.put("1", KeyEvent.VK_1);
        keyMap.put("2", KeyEvent.VK_2);
        keyMap.put("3", KeyEvent.VK_3);
        keyMap.put("4", KeyEvent.VK_4);
        keyMap.put("5", KeyEvent.VK_5);
        keyMap.put("6", KeyEvent.VK_6);
        keyMap.put("7", KeyEvent.VK_7);
        keyMap.put("8", KeyEvent.VK_8);
        keyMap.put("9", KeyEvent.VK_9);
        keyMap.put("enter", KeyEvent.VK_ENTER);
        keyMap.put("backspace", KeyEvent.VK_BACK_SPACE);
        keyMap.put("space", KeyEvent.VK_SPACE);

        // Press and release the keys in the specified combination
        try {
            for (String key : args) {
                Integer keyCode = keyMap.get(key.toLowerCase()); // Get the key code from the map
                if (keyCode != null) {
                    robot.keyPress(keyCode); // Press the key
                } else {
                    log.log("Unknown key: " + key, "error");
                }
            }
            // Release the keys in the reverse order
            for (String key : args) {
                Integer keyCode = keyMap.get(key.toLowerCase());
                if (keyCode != null) {
                    robot.keyRelease(keyCode); // Release the key
                }
            }
        } catch (Exception e) {
            log.log("Error pressing keys: " + e.getMessage(), "error");
        }
    }

    // Function to open an application by path
    public void openApp(List<String> args) {
        if (args.size() == 1) {
            String appPath = args.get(0); // Get the application path
            try {
                ProcessBuilder processBuilder = new ProcessBuilder(appPath); // Use ProcessBuilder to execute the application
                processBuilder.start(); // Start the process
            } catch (IOException e) {
                log.log("Error opening application: " + e.getMessage(), "error");
            }
        } else {
            log.log("Invalid arguments for openApp. Expected 1 argument (appPath).", "error");
        }
    }
    
    // Function to close an open application by name
    public void closeApp(List<String> args) {
        if (args.size() == 1) {
            String appName = args.get(0); // Get the application name
            try {
                // Use ProcessBuilder to execute a command to close the application
                ProcessBuilder processBuilder = new ProcessBuilder("taskkill", "/IM", appName, "/F");
                processBuilder.start(); // Start the process
            } catch (IOException e) {
                log.log("Error closing application: " + e.getMessage(), "error");
            }
        } else {
            log.log("Invalid arguments for closeApp. Expected 1 argument (appName).", "error");
        }
    }

    // Function to wait for a specific window to appear
    // public void waitForWindow(List<String> args) {
    //     if (args.size() == 1) {
    //         String windowName = args.get(0); // Get the window name
    //         // Implement logic to check for window presence (placeholder)
    //         // This is system-dependent and might require additional libraries
    //         log.log("Waiting for window: " + windowName, "info");
    //         // You can implement actual waiting logic depending on your OS and environment
    //         // Example (pseudo-code):
    //         // while (!isWindowOpen(windowName)) {
    //         //     Thread.sleep(1000); // Wait for 1 second before checking again
    //         // }
    //     } else {
    //         log.log("Invalid arguments for waitForWindow. Expected 1 argument (windowName).", "error");
    //     }
    // }

    // Function to get the current content of the clipboard
    public String getClipboard() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard(); // Get the system clipboard
        Transferable contents = clipboard.getContents(null); // Get the clipboard contents
        if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                return (String) contents.getTransferData(DataFlavor.stringFlavor); // Return clipboard text
            } catch (Exception e) {
                log.log("Error getting clipboard content: " + e.getMessage(), "error");
            }
        }
        return ""; // Return empty if no text is found
    }

    // Function to set text into the clipboard
    public void setClipboard(List<String> args) {
        if (args.size() == 1) {
            String text = args.get(0); // Get the text to set into the clipboard
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard(); // Get the system clipboard
            clipboard.setContents(new java.awt.datatransfer.StringSelection(text), null); // Set clipboard content
        } else {
            log.log("Invalid arguments for setClipboard. Expected 1 argument (text).", "error");
        }
    }
}
