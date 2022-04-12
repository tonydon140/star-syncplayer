package top.tonydon.util;

import top.tonydon.message.server.ConnectMessage;
import top.tonydon.message.client.MovieMessage;
import top.tonydon.message.server.ServerBindMessage;

public interface ClientObserver extends Observer{
    void onConnected(ConnectMessage message);

    void onMovie(MovieMessage message);

    void onBind(ServerBindMessage message);
}
