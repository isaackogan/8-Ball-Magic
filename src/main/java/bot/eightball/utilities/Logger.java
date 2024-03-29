package bot.eightball.utilities;

import java.lang.management.ManagementFactory;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class Logger {

    public static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * Get the class that called this one
     * Via https://stackoverflow.com/questions/11306811/how-to-get-the-caller-class-in-java
     *
     * @return Class name called from
     */
    public static String getCallerCallerClassName() {
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        String callerClassName = null;
        for (int i = 1; i < stElements.length; i++) {
            StackTraceElement ste = stElements[i];
            if (ste.getClassName().indexOf("java.lang.Thread") != 0) {
                if (callerClassName == null) {
                    callerClassName = ste.getClassName();
                } else if (!callerClassName.equals(ste.getClassName())) {
                    return ste.getClassName();
                }
            }
        }
        return null;
    }

    /**
     * Create a basic info log
     *
     * @param text   Text for the log
     * @param values Any values to format text with
     */
    private static void log(String text, Object... values) {
        log(text, LogLevel.INFO, values);
    }

    public static void INFO(String text, Object... values) {
        log(text, LogLevel.INFO, values);
    }

    public static void WARN(String text, Object... values) {
        log(text, LogLevel.WARN, values);
    }

    public static void ERROR(String text, Object... values) {
        log(text, LogLevel.ERROR, values);
    }

    public static void DEBUG(String text, Object... values) {
        log(text, LogLevel.DEBUG, values);
    }

    /**
     * Create a log with a custom log level
     *
     * @param text   Text to log
     * @param level  The logging level
     * @param values Values to format the text with
     */
    private static void log(String text, LogLevel level, Object... values) {
        String process = ManagementFactory.getRuntimeMXBean().getName();
        String pid = process.substring(0, process.indexOf("@"));

        String[] classes = Objects.requireNonNull(getCallerCallerClassName()).split("\\.");
        List<String> formattedClasses = new LinkedList<>();

        IntStream.range(0, classes.length)
                .mapToObj(index -> index == classes.length - 1 ? classes[index] : classes[index].substring(0, 3))
                .forEach(formattedClasses::add);

        System.out.printf(
                "%s %s %s --- %s %s%n",
                dtf.format(LocalDateTime.now(ZoneId.of("UTC"))),
                level.colour + level.label + "\u001B[0m",
                "\u001B[35m" + pid + "\u001B[0m",
                "\u001B[36m" + String.join(".", formattedClasses) + "\u001B[0m\t:",
                String.format(text, values)
        );

    }

    /**
     * Various logging levels
     */
    public enum LogLevel {

        INFO("INFO", "\u001B[32m"),
        DEBUG("DEBUG", "\u001B[32m"),
        WARN("WARN", "\u001B[33m"),
        ERROR("ERROR", "\u001B[31m");

        public String label;
        public String colour;

        LogLevel(String label, String colour) {
            this.label = label;
            this.colour = colour;
        }
    }

}
