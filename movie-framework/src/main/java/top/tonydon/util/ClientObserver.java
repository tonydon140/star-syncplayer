package top.tonydon.util;

import top.tonydon.message.server.ServerConnectMessage;
import top.tonydon.message.client.ClientMovieMessage;
import top.tonydon.message.server.ServerBindMessage;
import top.tonydon.message.server.ServerOfflineMessage;
import top.tonydon.message.server.ServerUnbindMessage;

public interface ClientObserver extends Observer{
    void onConnected(ServerConnectMessage message);

    void onMovie(ClientMovieMessage message);

    void onBind(ServerBindMessage message);

    void onUnBind(ServerUnbindMessage message);

    void onOffline(ServerOfflineMessage message);

}
