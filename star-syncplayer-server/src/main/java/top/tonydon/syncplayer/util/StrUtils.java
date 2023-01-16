package top.tonydon.syncplayer.util;

import java.util.Random;

public class StrUtils {
    public static String randomNum(int length) {
        if (length <= 0) return "";

        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
