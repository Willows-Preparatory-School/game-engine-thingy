package Engine;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

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

    public static float[] listFloatToArray(List<Float> list) {
        int size = list != null ? list.size() : 0;
        float[] floatArr = new float[size];
        for (int i = 0; i < size; i++) {
            floatArr[i] = list.get(i);
        }
        return floatArr;
    }

    public static int[] listIntToArray(List<Integer> list) {
        return list.stream().mapToInt((Integer v) -> v).toArray();
    }
}
