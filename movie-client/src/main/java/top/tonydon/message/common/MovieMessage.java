package top.tonydon.message.common;

import top.tonydon.message.Message;
import top.tonydon.util.MessageType;

/**
 * 控制电影相关的信息
 * <p>
 * actionCode：动作码
 * duration：当前视频的时间
 * rate: 当前视频的部分速度
 */
public class MovieMessage extends Message {

    private int actionCode;

    private double seconds;

    private double rate;

    public MovieMessage() {

    }

    public MovieMessage(int actionCode) {
        this.actionCode = actionCode;
    }

    public MovieMessage(int actionCode, double seconds, double rate) {
        this.actionCode = actionCode;
        this.seconds = seconds;
        this.rate = rate;
    }

    @Override
    public int getType() {
        return MessageType.MOVIE;
    }

    public int getActionCode() {
        return actionCode;
    }

    public void setActionCode(int actionCode) {
        this.actionCode = actionCode;
    }

    public double getSeconds() {
        return seconds;
    }

    public void setSeconds(double seconds) {
        this.seconds = seconds;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    @Override
    public String toString() {
        return "MovieMessage{" +
                "actionCode=" + actionCode +
                ", seconds=" + seconds +
                ", rate=" + rate +
                "} " + super.toString();
    }
}
