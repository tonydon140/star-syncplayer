package top.tonydon.util;

import javafx.util.Duration;

public class DurationUtils {

    private static String total = "";


    public static void setTotal(Duration duration) {
        total = convertDuration(duration.toSeconds());
    }

    private static String convertDuration(double dSeconds) {
        int seconds = (int) dSeconds;

        int minutes = 0;
        while (seconds >= 60) {
            minutes++;
            seconds -= 60;
        }

        String minutesStr = String.valueOf(minutes);
        if (minutesStr.length() == 1) minutesStr = "0" + minutesStr;

        String secondsStr = String.valueOf(seconds);
        if (secondsStr.length() == 1) secondsStr = "0" + secondsStr;

        return minutesStr + ":" + secondsStr;
    }

    public static String getText(double seconds) {
        return convertDuration(seconds) + "/" + total;
    }

    public static String getText(Duration duration) {
        return getText(duration.toSeconds());
    }
}
