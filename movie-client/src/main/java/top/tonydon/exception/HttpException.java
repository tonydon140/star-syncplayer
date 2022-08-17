package top.tonydon.exception;

public class HttpException extends RuntimeException {
    public HttpException(int code) {
        super("code = " + code);
    }
}
