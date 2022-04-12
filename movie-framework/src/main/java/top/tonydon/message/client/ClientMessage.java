package top.tonydon.message.client;

import top.tonydon.message.Message;

public abstract class ClientMessage extends Message {
    private String selfNumber;

    public ClientMessage(String selfNumber) {
        this.selfNumber = selfNumber;
    }

    public String getSelfNumber() {
        return selfNumber;
    }

    public void setSelfNumber(String selfNumber) {
        this.selfNumber = selfNumber;
    }

    @Override
    public String toString() {
        return "ClientMessage{" +
                "selfNumber='" + selfNumber + '\'' +
                '}';
    }
}
