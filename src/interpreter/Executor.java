package interpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import modules.*;
import functions.*;

public class Executor {

    private List<String> tokens;
    private int index;
    private Map<String, String> functions;

    logger log = new logger();
    fns fn = new fns();
    Conditions evaluvate = new Conditions();

    public Executor(List<String> tokens) {
        this.tokens = tokens;
        this.index = 0;
        this.functions = new HashMap<>();

        //for print functions
        functions.put("print", "print");
        functions.put("display", "print");
        functions.put("show", "print");
        functions.put("say", "print");
        functions.put("output", "print");
        functions.put("write", "print");
        functions.put("output", "print");


    }

    public void execute() {
        while (index < tokens.size()) {
            String token = tokens.get(index);
    
            if (token.equals("if")) {
                String cond = "";
                List<String> block = new ArrayList<>();
    
                index++;
                while (index < tokens.size() && !tokens.get(index).equals("\\n") && !tokens.get(index).equals("then")) {
                    cond += tokens.get(index) + " ";
                    index++;
                }
    
                index++;
                while (index < tokens.size() && !tokens.get(index).equals("\\n") && !tokens.get(index).equals("endif")) {
                    block.add(tokens.get(index));
                    index++;
                }
                block.add("\\n");
                if (tokens.get(index).equals("\\n")) {
                    index++;
                }
                if (tokens.get(index).equals("endif")) {
                    index++;
                }
    
                if (evaluvate.evaluate(cond)) {
                    Executor blockExecutor = new Executor(block);
                    blockExecutor.execute();
                }
            } else if (functions.containsKey(token) && index + 1 < tokens.size()) {

                if (functions.get(token).equals("print")) {
                    index++;
                    
                    List<String> args = new ArrayList<String>();
                    while (index < tokens.size() && !tokens.get(index).equals("\\n")) {
                        args.add(tokens.get(index));
                        index++;
                    }

                    if (tokens.get(index).equals("\\n")) index++;

                    fn.print(args);
                }

            } else if (token.equals("\\n")){
                index++;
            } else {
                log.log("Unknown token: " + token + " at pos " + index, "error");
                index++;
            }
        }
    }
    
}
