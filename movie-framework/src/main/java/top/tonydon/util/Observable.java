package top.tonydon.util;

/**
 * 被观察者接口
 */
public interface Observable {
    /**
     * 添加观察者
     *
     * @param observer 观察者对象
     */
    void addObserver(ClientObserver observer);

    /**
     * 删除观察者
     *
     * @param observer 观察者对象
     */
    void removeObserver(ClientObserver observer);
}
