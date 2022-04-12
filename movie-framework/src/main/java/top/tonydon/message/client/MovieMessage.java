package top.tonydon.message.client;

import top.tonydon.message.Message;
import top.tonydon.util.MessageType;

public class MovieMessage extends Message {

    @Override
    public int getType() {
        return MessageType.MOVIE_TYPE;
    }
}
