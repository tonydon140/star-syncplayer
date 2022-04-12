package top.tonydon.util;

import top.tonydon.message.server.ConnectMessage;
import top.tonydon.message.client.MovieMessage;

public interface ClientObserver extends Observer{
    void onConnected(ConnectMessage message);

    void onMovie(MovieMessage message);
}
