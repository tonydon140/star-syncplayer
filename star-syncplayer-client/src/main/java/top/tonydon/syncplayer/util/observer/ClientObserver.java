package top.tonydon.syncplayer.util.observer;


import top.tonydon.syncplayer.message.common.MovieMessage;

public interface ClientObserver {
    void onAction(int code);

    void onString(int code, String content);

    void onMovie(MovieMessage message);

}
