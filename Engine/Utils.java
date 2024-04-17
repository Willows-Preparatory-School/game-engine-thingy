package Engine;

import java.io.IOException;
import java.nio.file.*;

public class Utils {

    private Utils() {
        // Utility class
    }

    public static String readFile(String filePath) {
        String str;
        try {
            str = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException excp) {
            throw new RuntimeException("Error reading file [" + filePath + "]", excp);
        }
        return str;
    }

    public static enum logType
    {
        LOG,
        WARN,
        ERROR,
        DEBUG
    };
    public static void log(logType logLevel, String msg)
    {
        switch (logLevel) {
            case LOG -> System.out.println("[LOG]: " + msg);
            case WARN -> System.out.println("[WARN]: " + msg);
            case ERROR -> System.out.println("[ERROR]: " + msg);
            case DEBUG -> System.out.println("[DEBUG]: " + msg);
        }
    }
}
