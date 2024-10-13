package interpreter;

import java.util.ArrayList;
import java.util.List;

public class Lexer {

    private String input;
    private List<String> tokens;

    public Lexer(String input) {
        this.input = input;
        this.tokens = new ArrayList<>();
    }

    public List<String> tokenize() {
        StringBuilder currentWord = new StringBuilder();
        boolean inString = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (inString) {
                if (c == '"') {
                    inString = false;
                    currentWord.append(c);
                    tokens.add(currentWord.toString());
                    currentWord.setLength(0);
                } else {
                    currentWord.append(c);
                }
            } else {
                if (c == '"') {
                    if (currentWord.length() > 0) {
                        tokens.add(currentWord.toString());
                        currentWord.setLength(0);
                    }
                    inString = true;
                    currentWord.append(c);
                } else if (c == '\n') {
                    if (currentWord.length() > 0) {
                        tokens.add(currentWord.toString());
                        currentWord.setLength(0);
                    }
                    tokens.add("\\n");
                } else if (Character.isWhitespace(c)) {
                    if (currentWord.length() > 0) {
                        tokens.add(currentWord.toString());
                        currentWord.setLength(0);
                    }
                } else {
                    currentWord.append(c);
                }
            }
        }

        if (currentWord.length() > 0) {
            tokens.add(currentWord.toString());
        }

        return tokens;
    }

    // public static void main(String[] args) {
    //     Lexer lexer = new Lexer("print \"my world\" 123 true\nif condition then\n...\nend\n");
    //     List<String> tokens = lexer.tokenize();
    //     System.out.println(tokens);
    // }
}
