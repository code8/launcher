package code8.launcher;

import code8.launcher.logic.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.PeriodicTrigger;

@Configuration
public class SchedulerConfig implements SchedulingConfigurer {
    private final int POOL_SIZE = 1;

    private final OrderService orderService;

    @Autowired
    public SchedulerConfig(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        ThreadPoolTaskScheduler orderProcessingScheduller = new ThreadPoolTaskScheduler();
        orderProcessingScheduller.setPoolSize(POOL_SIZE);
        orderProcessingScheduller.setThreadNamePrefix("order-processing-pool-");
        orderProcessingScheduller.initialize();
        orderProcessingScheduller.schedule(orderService::processMarketOrders, new PeriodicTrigger(1000));
        scheduledTaskRegistrar.setTaskScheduler(orderProcessingScheduller);
    }
}
