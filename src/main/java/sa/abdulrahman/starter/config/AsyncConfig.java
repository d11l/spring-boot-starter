package sa.abdulrahman.starter.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);       // number of core threads
        executor.setMaxPoolSize(10);       // max threads
        executor.setQueueCapacity(100);    // tasks queue
        executor.setThreadNamePrefix("Async-");
        executor.initialize();
        return executor;
    }
}