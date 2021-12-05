package bot.eightball.utilities;

import io.sentry.util.StringUtils;

import javax.annotation.Nullable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MiscUtils {

    final static String[] numberFormatMagnitudes = new String[]{"", "K", "M", "B", "T", "QD", "QN"};

    public static String commaFormatNumber(long number) {
        return NumberFormat.getNumberInstance(Locale.US)
                .format(number);
    }

    public static String roundNumber(double number, int decimals) {
        final String precision = "%" + String.format(".%sf", decimals);
        return String.format(precision, number);
    }

    public static String fancyFormatNumber(double number, int decimals, boolean upperCase) {

        if (number < 10000) {
            return commaFormatNumber((long) number);
        }

        int magnitude = 0;
        while (Math.abs(number) >= 1000) {
            magnitude += 1;
            number /= 1000D;
        }

        String result = (roundNumber(number, decimals) + numberFormatMagnitudes[magnitude]);
        return upperCase ? result.toUpperCase(Locale.ROOT) : result;

    }

    public static String fancyFormatNumber(long number, int decimals) {
        return fancyFormatNumber(number, decimals, true);
    }

    public static String fancyFormatNumber(long number, boolean upperCase) {
        return fancyFormatNumber(number, 1, upperCase);
    }

    public static String fancyFormatNumber(long number) {
        return fancyFormatNumber(number, 1, true);
    }

    public static <T> List<List<T>> chunk(List<T> bigList, int n) {
        List<List<T>> chunks = new ArrayList<>();

        for (int i = 0; i < bigList.size(); i += n) {
            @SuppressWarnings("unchecked") T[] chunk = (T[]) bigList.subList(i, Math.min(bigList.size(), i + n)).toArray();
            chunks.add(Arrays.asList(chunk));
        }

        return chunks;
    }

    public static String capStringSize(@Nullable String string, int length) {
        string = (string == null) ? "" : string;
        return (string.length() > length) ? string.substring(0, Math.max(0, length - 2)) + "..." : string;
    }

    public static boolean isPopulated(String string, boolean strip) {
        if (string == null) return false;
        string = strip ? string.strip() : string;
        return string.length() >= 1;
    }

    public static String formatMarkdown(String string) {
        string = string.replaceAll("`", "");
        return (string.contains("_") || string.contains("*")) ? "`" + string + "`" : string;
    }

    public static String capitalizeWords(String string) {
        String[] sub = string.split(" ");
        String[] rep = new String[sub.length];

        for (int i = 0; i < sub.length; i++)
            rep[i] = StringUtils.capitalize(sub[i]);

        return String.join(" ", rep);
    }

    public static String clockFormatSeconds(int seconds) {
        return String.format("%02d:%02d", (seconds / 60) % 60, seconds % 60);
    }


}
