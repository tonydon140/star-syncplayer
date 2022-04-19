package top.tonydon.util;

import lombok.Data;

@Data
public class ResponseResult {
    private Integer code;
    private Object data;
    private String msg;

    public ResponseResult(Integer code, Object data) {
        this.code = code;
        this.data = data;
    }

    public ResponseResult(Integer code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ResponseResult(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static ResponseResult success() {
        return new ResponseResult(200, "操作成功");
    }

    public static ResponseResult success(Object data) {
        return new ResponseResult(200, "操作成功", data);
    }
}
