package top.tonydon.syncplayer.message;

public class ActionCode {

    /**************************************************************************
     * <p>
     * ActionMessage
     *
     **************************************************************************/
    public static final int UNBIND = 101;
    public static final int OFFLINE = 102;
    public static final int CREATE_ROOM = 103;
    public static final int QUIT_ROOM = 104;


    /**************************************************************************
     * <p>
     * StringMessage
     *
     **************************************************************************/
    public static final int BIND = 201;
    public static final int CONNECTED = 202;
    public static final int BULLET_SCREEN = 203;
    public static final int SERVER_RESPONSE = 204;
    public static final int ROOM_CREATED = 205;
    public static final int ADD_ROOM = 206;
    public static final int CHAT = 207;



    /**************************************************************************
     * <p>
     * MovieMessage
     *
     **************************************************************************/
    public static final int MOVIE_PLAY = 301;
    public static final int MOVIE_PAUSE = 302;
    public static final int MOVIE_STOP = 303;
    public static final int MOVIE_SYNC = 304;

}
