package modules;

public class logger {

    public Boolean l;

    public logger(){
        this.l = true;
    }

    public static final String RED = "\033[0;31m";
    public static final String YELLOW = "\033[0;33m";
    public static final String RESET = "\033[0m";

    public void log(String message, String type) {
        switch (type) {
            case "warn":
                if (this.l) System.out.println(YELLOW + "WARNING: " + message + RESET);
                break;
            case "error":
                System.out.println(RED + "ERROR: " + message + RESET);
                System.exit(1);
            default:
                System.out.println(message);
        }
    }
}
