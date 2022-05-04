package top.tonydon.task;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ControllerService extends ScheduledService<Number> {
     private final Logger log = LoggerFactory.getLogger(ControllerService.class);

    private int sum = 0;

    @Override
    protected Task<Number> createTask() {
        return new Task<>() {
            @Override
            protected Number call() throws Exception {
                sum++;
                if (sum > 24 * 60 * 60) sum = 0;
                log.debug("sum = {}", sum);
                return sum;
            }
        };
    }

    public void setSum(int sum) {
        this.sum = sum;
    }
}
