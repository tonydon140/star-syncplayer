package top.tonydon.message.client;

import top.tonydon.message.Message;
import top.tonydon.util.MessageType;

public class ClientMovieMessage extends Message {

    @Override
    public int getType() {
        return MessageType.CLIENT_MOVIE;
    }
}
