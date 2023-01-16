package top.tonydon.syncplayer.exception;

public class HttpException extends RuntimeException {
    public HttpException(int code) {
        super("code = " + code);
    }
}
