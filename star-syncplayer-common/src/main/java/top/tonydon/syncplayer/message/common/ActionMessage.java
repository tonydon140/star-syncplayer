package top.tonydon.syncplayer.message.common;

import top.tonydon.syncplayer.message.ActionCode;
import top.tonydon.syncplayer.message.Message;
import top.tonydon.syncplayer.message.MessageType;

/**
 * 动作消息，表示没有携带信息，只表达其动作的消息。如解除绑定、下线等
 * 其含义通过 actionCode 表达
 */
public class ActionMessage extends Message {

    public static final ActionMessage UNBIND = new ActionMessage(ActionCode.UNBIND);
    public static final ActionMessage OFFLINE = new ActionMessage(ActionCode.OFFLINE);

    public static final ActionMessage CREATE_ROOM = new ActionMessage(ActionCode.CREATE_ROOM);
    public static final ActionMessage QUIT_ROOM = new ActionMessage(ActionCode.QUIT_ROOM);
//    public static final ActionMessage MOVIE_PAUSE = new ActionMessage(ActionCode.QUIT_ROOM);

    private int actionCode;

    public ActionMessage(){

    }

    public ActionMessage(int actionCode) {
        this.actionCode = actionCode;
    }

    public int getActionCode() {
        return actionCode;
    }

    public void setActionCode(int actionCode) {
        this.actionCode = actionCode;
    }

    @Override
    public int getType() {
        return MessageType.ACTION;
    }

    @Override
    public String toString() {
        return "ActionMessage{" +
                "actionCode=" + actionCode +
                "} " + super.toString();
    }
}
