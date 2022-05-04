package top.tonydon.task;

import top.tonydon.util.observer.CountObserver;

import java.util.concurrent.TimeUnit;

public class CountTask {
    private final CountRunnable countRunnable;
    private final Thread thread;

    public CountTask() {
        countRunnable = new CountRunnable(TimeUnit.SECONDS, 1);
        thread = new Thread(countRunnable, "task");
        thread.setDaemon(true);
    }

    public CountTask(TimeUnit timeUnit, long timeout) {
        countRunnable = new CountRunnable(timeUnit, timeout);
        thread = new Thread(countRunnable, "task");
        thread.setDaemon(true);
    }

    public void ready(){
        countRunnable.stop();
        if (!thread.isAlive())
            thread.start();
    }

    public void start() {
        if (!thread.isAlive())
            thread.start();
    }

    public void cancel() {
        if (thread.isAlive())
            thread.interrupt();
    }

    public void stop() {
        countRunnable.stop();
    }

    public boolean isStop() {
        return countRunnable.isStop();
    }

    public void restart() {
        countRunnable.restart();
    }

    public void setCount(int count){
        countRunnable.setCount(count);
    }

    public void addObserver(CountObserver observer) {
        countRunnable.addObserver(observer);
    }

    public void removeObserver(CountObserver observer) {
        countRunnable.removeObserver(observer);
    }
}
