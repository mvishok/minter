import interpreter.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class minter {
    public static void main(String[] args) {
        
        String code = "";

        if (args.length == 0) {
            repl();
        } 

        try {
            code = Files.readString(Paths.get(args[0]));
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        
        Lexer lexer = new Lexer(code);
        List<String> tokens = lexer.tokenize();
        Executor executor = new Executor(tokens);
        executor.execute();
    }

    public static void repl() {
        System.out.println("Minimal INTerpretER (MINTER) v0.1 REPL\n");
        while (true) {
            System.out.print("mint> ");
            try (Scanner scanner = new Scanner(System.in)) {
                String input = scanner.nextLine();
                Lexer lexer = new Lexer(input);
                List<String> tokens = lexer.tokenize();
                Executor executor = new Executor(tokens);
                executor.execute();
            }
        }
    }
}
