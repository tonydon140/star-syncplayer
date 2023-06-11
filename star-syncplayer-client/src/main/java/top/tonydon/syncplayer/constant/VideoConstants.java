package top.tonydon.syncplayer.constant;

import java.util.List;

public class VideoConstants {

    public static final int DEFAULT_VOLUME = 80;
    public static final float DEFAULT_RATE = 1.0f;

    public static final List<String> VIDEO_FILTER = List.of(
            "*.3g2", "*.3ga", "*.3gp","*.avi", "*.flv",
            "*.h264", "*.m4a", "*.mov", "*.mpeg", "*.mts", "*.ogg",
            "*.opus", "*.ts", "*.webm", "*.wmv", "*.mkv", "*.mp4"
    );
}
