package functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;


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

    private Connection connection = null; // Database connection
    private Statement statement = null; // SQL statement

    private logger log = new logger(); // Logger instance

    private MemoryManager memoryManager; // Reference to MemoryManager

    private Scanner scanner = new Scanner(System.in); // Scanner for user input

    private Robot robot; // Robot instance for automation tasks

    public fns(MemoryManager memoryManager) {
        String url = "jdbc:mysql://localhost:3306/minter";  // Replace with your database URL
        String user = "root";  // Replace with your database username
        String password = "";  // Replace with your database password

        try {
            // Establish the connection and create the statement object
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();

        } catch (SQLException e) {
            log.log("SQL Error: " + e.getMessage(), "error");
        }

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
    
        //String firstArg = args.get(0); // Get the first argument, which could be a variable or function name
        // Check if the first argument is a function that needs to be executed

        //go through all the args and replace the "val" with the value of the variable or function 

        StringBuilder results = new StringBuilder(); // StringBuilder to store results

        for (String arg : args) {
            try {
            Method method = this.getClass().getDeclaredMethod(arg); // Look for the function by name
            Object result = method.invoke(this); // Call the function with no parameters
            results.append(result.toString()).append(" "); // Append the result to the StringBuilder with a space
            } catch (NoSuchMethodException e) {
            // If it's not a function, check if it's a variable stored in memory
            if (memoryManager.exists(arg)) {
                results.append(memoryManager.get(arg)).append(" "); // Append the value of the variable from memory with a space
            } else {
                results.append(arg).append(" "); // Append the argument as is if it's neither a function nor a variable with a space
            }
            } catch (Exception e) {
            e.printStackTrace(); // Handle other exceptions
            results.append(""); // Append an empty string or handle the error as needed
            }
        }

        return results.toString().trim(); // Return the final string, trimming any trailing spaces
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

    // Function to simulate a middle mouse click
    public void middleClick() {
        robot.mousePress(InputEvent.BUTTON2_DOWN_MASK); // Simulate middle mouse press
        robot.mouseRelease(InputEvent.BUTTON2_DOWN_MASK); // Simulate middle mouse release
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
        // if args contains "val" then get the value of the variable
        for (int i = 0; i < args.size(); i++) {
            if (args.get(i).equals("val")) {
                List<String> valArgs = args.subList(i + 1, args.size()); // Get all args after 'val'
                String valueOutput = val(valArgs).toString(); // Get the value
                args.set(i, valueOutput); // Replace "val" with the value
            }
        }

        for (String arg : args) {
            for (char c : arg.toCharArray()) {
                try {
                    robot.keyPress(KeyEvent.getExtendedKeyCodeForChar(c)); // Simulate key press
                    robot.keyRelease(KeyEvent.getExtendedKeyCodeForChar(c)); // Simulate key release
                } catch (IllegalArgumentException e) {
                    switch (c) {
                        case '?':
                            robot.keyPress(KeyEvent.VK_SHIFT);
                            robot.keyPress(KeyEvent.VK_SLASH);
                            robot.keyRelease(KeyEvent.VK_SLASH);
                            robot.keyRelease(KeyEvent.VK_SHIFT);
                            break;
                        case '%':
                            robot.keyPress(KeyEvent.VK_SHIFT);
                            robot.keyPress(KeyEvent.VK_5);
                            robot.keyRelease(KeyEvent.VK_5);
                            robot.keyRelease(KeyEvent.VK_SHIFT);
                            break;
                        case ':':
                            robot.keyPress(KeyEvent.VK_SHIFT);
                            robot.keyPress(KeyEvent.VK_SEMICOLON);
                            robot.keyRelease(KeyEvent.VK_SEMICOLON);
                            robot.keyRelease(KeyEvent.VK_SHIFT);
                            break;
                        case '&':
                            robot.keyPress(KeyEvent.VK_SHIFT);
                            robot.keyPress(KeyEvent.VK_7);
                            robot.keyRelease(KeyEvent.VK_7);
                            robot.keyRelease(KeyEvent.VK_SHIFT);
                            break;
                        case '=':
                            robot.keyPress(KeyEvent.VK_EQUALS);
                            robot.keyRelease(KeyEvent.VK_EQUALS);
                            break;
                        case '+':
                            robot.keyPress(KeyEvent.VK_SHIFT);
                            robot.keyPress(KeyEvent.VK_EQUALS);
                            robot.keyRelease(KeyEvent.VK_EQUALS);
                            robot.keyRelease(KeyEvent.VK_SHIFT);
                            break;
                        case '-':
                            robot.keyPress(KeyEvent.VK_MINUS);
                            robot.keyRelease(KeyEvent.VK_MINUS);
                            break;
                        case '.':
                            robot.keyPress(KeyEvent.VK_PERIOD);
                            robot.keyRelease(KeyEvent.VK_PERIOD);
                            break;
                        case '/':
                            robot.keyPress(KeyEvent.VK_SLASH);
                            robot.keyRelease(KeyEvent.VK_SLASH);
                            break;
                        // Add more cases as needed
                        default:
                            log.log("Unsupported character: " + c, "error");
                    }
                }
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
        //args must be greater than 0
        if (args.size() == 0) {
            log.log("Invalid arguments for pressKeys. Expected atleast 1 argument.", "error");
            return;
        }
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
        keyMap.put("tab", KeyEvent.VK_TAB);
        keyMap.put("esc", KeyEvent.VK_ESCAPE);
        keyMap.put("ins", KeyEvent.VK_INSERT);

        keyMap.put("plus", KeyEvent.VK_PLUS);
        keyMap.put("f1", KeyEvent.VK_F1);


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
        if (args.size() > 0) {
            String appPath = String.join(" ", args);
            try {
                ProcessBuilder processBuilder = new ProcessBuilder(appPath); // Use ProcessBuilder to execute the application
                processBuilder.start(); // Start the process
            } catch (IOException e) {
                log.log("Error opening application: " + e.getMessage(), "error");
            }
        } else {
            log.log("Invalid arguments for openApp. Expected atleast 1 argument (appPath).", "error");
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

// Create table function which now uses the global `connection` and `statement`
    public void create(List<String> args) {
        if (connection == null || statement == null) {
            log.log("Database connection is not initialized.", "error");
            return;
        }

        if (args.isEmpty()) {
            log.log("No arguments provided for table creation.", "error");
            return;
        }

        String tableName = args.get(0); // The first argument is the table name
        List<String> columns = args.subList(1, args.size()); // Remaining arguments are column definitions

        // Build the SQL create table query
        StringBuilder queryBuilder = new StringBuilder("CREATE TABLE " + tableName + " (");

        for (int i = 0; i < columns.size(); i++) {
            String[] columnDefinition = columns.get(i).split(":");

            if (columnDefinition.length != 2) {
                log.log("Invalid column definition: " + columns.get(i), "error");
                return;
            }

            String columnName = columnDefinition[0];
            String columnType = columnDefinition[1].toLowerCase();

            // Map custom types to SQL types
            switch (columnType) {
                case "int":
                    columnType = "INT";
                    break;
                case "str":
                    columnType = "VARCHAR(255)";
                    break;
                case "text":
                    columnType = "TEXT";
                    break;
                case "bool":
                    columnType = "BOOLEAN";
                    break;
                default:
                    log.log("Unsupported column type: " + columnType, "error");
                    return;
            }

            queryBuilder.append(columnName).append(" ").append(columnType);

            if (i < columns.size() - 1) {
                queryBuilder.append(", ");
            }
        }

        queryBuilder.append(");"); // Close the CREATE TABLE statement

        try {
            // Execute the SQL query using the global `statement`
            statement.executeUpdate(queryBuilder.toString());

        } catch (SQLException e) {
            // Handle SQL exceptions
            log.log("SQL Error: " + e.getMessage(), "error");
        }
    }    

    // Function to check if a table exists
    public int tableExists(List<String> args) {
        if (connection == null || statement == null) {
            log.log("Database connection is not initialized.", "error");
            return 0;
        }

        if (args.isEmpty()) {
            log.log("No arguments provided for checking table existence.", "error");
            return 0;
        }

        String tableName = args.get(0); // The first argument is the table name
        String query = "SHOW TABLES LIKE '" + tableName + "';"; // SQL query to check if the table exists

        ResultSet resultSet = null;
        
        try {
            // Execute the query and retrieve the result
            resultSet = statement.executeQuery(query);

            // If a result is found, the table exists
            if (resultSet.next()) {
                return 1; // Table exists
            } else {
                return 0; // Table does not exist
            }

        } catch (SQLException e) {
            // Handle SQL exceptions
            log.log("SQL Error: " + e.getMessage(), "error");
            return 0;
        } finally {
            try {
                if (resultSet != null) resultSet.close();
            } catch (SQLException e) {
                log.log("Error closing result set: " + e.getMessage(), "error");
            }
        }
    }

    // Insert function
    public void insert(List<String> args) {
        if (connection == null || statement == null) {
            log.log("Database connection is not initialized.", "error");
            return;
        }
    
        if (args.isEmpty()) {
            log.log("No arguments provided for insert operation.", "error");
            return;
        }
    
        String tableName = args.get(0); // The first argument is the table name
        List<String> values = args.subList(1, args.size()); // Remaining arguments are values to be inserted
    
        // Prepare the values for the SQL insert query
        StringBuilder queryBuilder = new StringBuilder("INSERT INTO " + tableName + " VALUES (");
    
        for (int i = 0; i < values.size(); i++) {
            String value = values.get(i).trim();
    
            // Check if the value is a variable
            if (memoryManager.exists(value)) {
                value = memoryManager.get(value); // Get the value of the variable
            }
    
            // Sanitize and format the value before inserting
            String sanitizedValue = sanitizeValue(value);
            queryBuilder.append(sanitizedValue);
    
            if (i < values.size() - 1) {
                queryBuilder.append(", ");
            }
        }
    
        queryBuilder.append(");"); // Close the INSERT statement
    
        try {
            // Execute the SQL query using a prepared statement
            PreparedStatement preparedStatement = connection.prepareStatement(queryBuilder.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            // Handle SQL exceptions
            log.log("SQL Error: " + e.getMessage(), "error");
        }
    }
    
    private String sanitizeValue(String value) {
        // Handle multiline strings and escape single quotes
        if (value.contains("\n")) {
            // If it's a multiline string, replace newlines with a space or other character
            value = value.replace("\n", " ");
        }
    
        // Escape single quotes by replacing them with two single quotes
        value = value.replace("'", "''");
    
        // Handle any other necessary sanitization here
    
        // Return the properly formatted string with quotes around it
        return "'" + value + "'";
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
