package com.mvishok;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

import interpreter.Executor;
import interpreter.Lexer;
import memory.MemoryManager;
import modules.logger;

public class minter {
    static logger log = new logger();

    public static void main(String[] args) {
        
        String code = "";

        if (args.length == 0) {
            repl();
        } 

        try {
            code = Files.readString(Paths.get(args[0]));
        } catch (IOException e) {
            log.log("Error reading file: " + e.getMessage(), "error");
        }
        
        Lexer lexer = new Lexer(code);
        List<String> tokens = lexer.tokenize();
        MemoryManager memoryManager = new MemoryManager();
        Executor executor = new Executor(tokens, memoryManager);
        executor.execute();
    }

    public static void repl() {
        log.log("Minimal INTerpretER (MINTER) v0.1 REPL\n", "");
        MemoryManager memoryManager = new MemoryManager();
        while (true) {
            System.out.print("mint> ");
            try (Scanner scanner = new Scanner(System.in)) {
                String input = scanner.nextLine();
                Lexer lexer = new Lexer(input);
                List<String> tokens = lexer.tokenize();
                Executor executor = new Executor(tokens, memoryManager);
                executor.execute();
            }
        }
    }
}
