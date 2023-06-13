package top.tonydon.syncplayer.util.observer

/**
 * 被观察者接口
 */
interface Observable<T> {
    /**
     * 添加观察者
     *
     * @param observer 观察者对象
     */
    fun addObserver(observer: T)

    /**
     * 删除观察者
     *
     * @param observer 观察者对象
     */
    fun removeObserver(observer: T)

}
