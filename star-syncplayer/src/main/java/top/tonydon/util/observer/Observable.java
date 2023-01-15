package top.tonydon.util.observer;

/**
 * 被观察者接口
 */
public interface Observable<T> {
    /**
     * 添加观察者
     *
     * @param observer 观察者对象
     */
    void addObserver(T observer);

    /**
     * 删除观察者
     *
     * @param observer 观察者对象
     */
    void removeObserver(T observer);
}
