package top.tonydon.message.client;

import top.tonydon.util.MessageType;

public class ClientUnbindMessage extends ClientMessage {
    public ClientUnbindMessage(String selfNumber) {
        super(selfNumber);
    }

    public ClientUnbindMessage() {

    }

    @Override
    public int getType() {
        return MessageType.CLIENT_UNBIND;
    }

    @Override
    public String toString() {
        return "ClientUnbindMessage{} " + super.toString();
    }
}
