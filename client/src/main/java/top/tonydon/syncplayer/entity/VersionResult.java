package top.tonydon.syncplayer.entity;

public class VersionResult {
    private Integer code;
    private String msg;
    private ProjectVersion data;

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data='" + data + '\'' +
                '}';
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ProjectVersion getData() {
        return data;
    }

    public void setData(ProjectVersion data) {
        this.data = data;
    }
}
