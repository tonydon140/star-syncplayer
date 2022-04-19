package top.tonydon.domain;

import javafx.util.Duration;

public class VideoDuration {


    private String currentDuration;

    private String totalDuration;

    public VideoDuration() {
        currentDuration = "00:00";
        totalDuration = "00:00";
    }

    public void setCurrentDuration(Duration duration) {
        currentDuration = convertDuration(duration);
    }

    public void setTotalDuration(Duration duration) {
        totalDuration = convertDuration(duration);
    }


    /**
     * 将 Duration 转化为 mm:ss 的时间格式
     *
     * @param duration Duration
     * @return 字符串时间格式
     */
    private String convertDuration(Duration duration) {
        int seconds = (int) duration.toSeconds();

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


    @Override
    public String toString() {
        return currentDuration + "/" + totalDuration;
    }
}
