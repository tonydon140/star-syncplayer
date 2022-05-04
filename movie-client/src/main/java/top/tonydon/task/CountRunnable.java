package top.tonydon.task;

import top.tonydon.util.observer.CountObserver;
import top.tonydon.util.observer.Observable;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class CountRunnable implements Runnable, Observable<CountObserver> {

    private int count;
    private boolean isStop;

    private final Set<CountObserver> observerSet = new HashSet<>();

    private final TimeUnit timeUnit;
    private final long timeout;


    public CountRunnable(TimeUnit timeUnit, long timeout){
        if (timeout <=0) throw new RuntimeException("间隔时间不能小于等于0");
        this.timeUnit = timeUnit;
        this.timeout = timeout;
    }
    
    @Override
    public void run() {
        // 线程没有被打断一直运行
        while (!Thread.currentThread().isInterrupted()) {
            if (isStop){
                count = 1;
                synchronized (this){
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }

            // 休眠线程
            try {
                timeUnit.sleep(timeout);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // 修改 count 值
            int temp = count;
            synchronized (this){
                count++;
                if(count == Integer.MAX_VALUE) count = 0;
            }
            observerSet.forEach(observer -> observer.countChange(temp, count));
        }
    }


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        int temp = this.count;
        synchronized (this){
            this.count = count;
        }
        observerSet.forEach(observer -> observer.countChange(temp, this.count));
    }


    @Override
    public void addObserver(CountObserver observer) {
        observerSet.add(observer);
    }

    @Override
    public void removeObserver(CountObserver observer) {
        observerSet.remove(observer);
    }

    public boolean isStop() {
        return isStop;
    }

    public void stop() {
        isStop = true;
    }

    public void restart(){
        isStop = false;
        synchronized (this){
            this.notifyAll();
        }
    }
}