package top.tonydon.util;

import top.tonydon.message.Message;
import top.tonydon.ws.ServerWebSocket;

import java.io.IOException;


public class SocketGroup {

    private ServerWebSocket ownSocket;
    private ServerWebSocket friendSocket;

    public SocketGroup() {

    }

    public SocketGroup(ServerWebSocket ownSocket, ServerWebSocket friendSocket) {
        this.ownSocket = ownSocket;
        this.friendSocket = friendSocket;
    }

    public ServerWebSocket getOwnSocket() {
        return ownSocket;
    }

    public void setOwnSocket(ServerWebSocket ownSocket) {
        this.ownSocket = ownSocket;
    }

    public ServerWebSocket getFriendSocket() {
        return friendSocket;
    }

    public void setFriendSocket(ServerWebSocket friendSocket) {
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
