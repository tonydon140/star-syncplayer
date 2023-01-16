package top.tonydon.syncplayer.util;

import top.tonydon.syncplayer.message.Message;
import top.tonydon.syncplayer.websocket.SyncPlayerItem;

import java.io.IOException;


public class SocketGroup {

    private SyncPlayerItem ownSocket;
    private SyncPlayerItem friendSocket;

    public SocketGroup() {

    }

    public SocketGroup(SyncPlayerItem ownSocket, SyncPlayerItem friendSocket) {
        this.ownSocket = ownSocket;
        this.friendSocket = friendSocket;
    }

    public SyncPlayerItem getOwnSocket() {
        return ownSocket;
    }

    public void setOwnSocket(SyncPlayerItem ownSocket) {
        this.ownSocket = ownSocket;
    }

    public SyncPlayerItem getFriendSocket() {
        return friendSocket;
    }

    public void setFriendSocket(SyncPlayerItem friendSocket) {
        this.friendSocket = friendSocket;
    }

    public void sendFriend(String content) {
        try {
            this.friendSocket.getSession().getBasicRemote().sendText(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendFriend(Message message) {
        sendFriend(message.toJson());
    }

    public void removeFriendSocket() {
        this.friendSocket = null;
    }

}
