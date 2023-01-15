package top.tonydon.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.tonydon.util.observer.CountObserver;
import top.tonydon.util.observer.Observable;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 定时计数器 <br><br>
 * 根据指定的时间单位 timeUnit 和时间间隔 timeout 将内部的 count 持续自增<br>
 * count 值可被外界监听，任务可以暂停、继续、停止<br>
 * 一个 CountTask 实例只能启动一次任务
 * 任务是一个守护线程
 */
public class CountTask implements Observable<CountObserver> {
    // 日志
    private final Logger log = LoggerFactory.getLogger(CountTask.class);
    // 观察者集合
    private final Set<CountObserver> observerSet = new HashSet<>();
    // 任务线程
    private Thread thread;
    // 停止标记
    private volatile boolean isStop;
    // 暂停标记
    private volatile boolean isPause;
    // 启动标记
    private volatile boolean started;

    private final TimeUnit timeUnit;
    private final long timeout;
    private int count = 0;

    public CountTask(TimeUnit timeUnit, long timeout) {
        this.timeUnit = timeUnit;
        this.timeout = timeout;
    }

    /**
     * 启动任务
     */
    public void start() {
        // 只允许启动一次
        synchronized (this) {
            if (started) {
                log.info("任务已启动，只允许启动一次！");
                return;
            }
            started = true;
        }
        // 启动线程
        thread = new Thread(() -> {
            while (!isStop) {
                // 暂停线程
                if (isPause) {
                    synchronized (this) {
                        try {
                            this.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            // 若 wait 时被打断，且还是处于暂停状态，直接 continue
                            if (isPause) continue;
                        }
                    }
                }

                // 休眠线程
                try {
                    timeUnit.sleep(timeout);
                } catch (InterruptedException ignored) {
                    // 休眠时被打断，不是暂停就是停止
                    // 直接 continue 就行
                    continue;
                }

                // count 计数
                final int temp = count;
                synchronized (this) {
                    count++;
                    if (count == Integer.MAX_VALUE) count = 0;
                }
                observerSet.forEach(countObserver -> countObserver.countChange(temp, count));
            }
        });
        thread.setDaemon(true);
        thread.start();
    }


    public void stop() {
        isStop = true;
        thread.interrupt();
    }

    public void pause() {
        // 如果已经暂停直接返回
        if (isPause) return;
        // 暂停任务
        isPause = true;
        thread.interrupt();
    }

    /**
     * 继续任务
     */
    public void goOn() {
        if (!isPause) return;
        // 继续任务
        isPause = false;
        // 唤醒任务
        synchronized (this) {
            this.notify();
        }
    }

    @Override
    public void addObserver(CountObserver observer) {
        observerSet.add(observer);
    }

    @Override
    public void removeObserver(CountObserver observer) {
        observerSet.remove(observer);
    }
}
