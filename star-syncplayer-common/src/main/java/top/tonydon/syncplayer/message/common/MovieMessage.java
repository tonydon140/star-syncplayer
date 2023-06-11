package top.tonydon.syncplayer.message.common;

import top.tonydon.syncplayer.message.ActionCode;
import top.tonydon.syncplayer.message.Message;
import top.tonydon.syncplayer.message.MessageType;

/**
 * 控制电影相关的信息
 * <p>
 * actionCode：动作码
 * duration：当前视频的时间
 * rate: 当前视频的部分速度
 */
public class MovieMessage extends Message {

    public static final MovieMessage MOVIE_PLAY = new MovieMessage(ActionCode.MOVIE_PLAY);
    public static final MovieMessage MOVIE_PAUSE = new MovieMessage(ActionCode.MOVIE_PAUSE);
    public static final MovieMessage MOVIE_STOP = new MovieMessage(ActionCode.MOVIE_STOP);

    private int actionCode;

    private long milliseconds;

    private float rate;

    public MovieMessage() {

    }

    @Override
    public int getType() {
        return MessageType.MOVIE;
    }

    public MovieMessage(int actionCode) {
        this.actionCode = actionCode;
    }

    public MovieMessage(int actionCode, long milliseconds, float rate) {
        this.actionCode = actionCode;
        this.milliseconds = milliseconds;
        this.rate = rate;
    }

    public int getActionCode() {
        return actionCode;
    }

    public void setActionCode(int actionCode) {
        this.actionCode = actionCode;
    }

    public long getMilliseconds() {
        return milliseconds;
    }

    public void setMilliseconds(long milliseconds) {
        this.milliseconds = milliseconds;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    @Override
    public String toString() {
        return "MovieMessage{" +
                "actionCode=" + actionCode +
                ", milliseconds=" + milliseconds +
                ", rate=" + rate +
                '}';
    }
}
