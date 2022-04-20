package top.tonydon.util;


import top.tonydon.message.server.*;

public interface ClientObserver extends Observer {
    void onConnected(ServerConnectMessage message);

    void onMovie(ServerMovieMessage message);

    void onBind(ServerBindMessage message);

    void onUnBind(ServerUnbindMessage message);

    void onOffline(ServerOfflineMessage message);

}
