package modules;

public class type {
    logger log = new logger();

    public Object setType(String type, String value) {
        switch (type) {
            case "string":
                return value;
            case "number":
                try {
                    if (value.contains(".")) {
                        return Double.parseDouble(value);
                    } else {
                        return Integer.parseInt(value);
                    }
                } catch (NumberFormatException e) {
                    log.log("Invalid number: " + value, "error");
                }
            case "boolean":
                return Boolean.parseBoolean(value);

            case "list":
                return value.split(",");

            default:
                return value;
        }
    }
    
    public static String getType(String value) {
        if (Boolean.parseBoolean(value)) {
            return "boolean";
        } else if (value.contains(".")) {
            return "number";
        } else {
            try {
                Integer.parseInt(value);
                return "number";
            } catch (NumberFormatException e) {
                return "string";
            }
        }
    }

}
