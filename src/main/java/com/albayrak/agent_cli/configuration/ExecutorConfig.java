package com.albayrak.agent_cli.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

// Config: thread pool
@Configuration
public class ExecutorConfig {
    @Bean(name = "commandExecutor")
    public ThreadPoolTaskExecutor commandExecutor() {
        ThreadPoolTaskExecutor exe = new ThreadPoolTaskExecutor();
        exe.setCorePoolSize(50); // minimum eş zamanlı thread sayısı
        exe.setMaxPoolSize(100); // maksimum
        exe.setQueueCapacity(200); // kuyruk limiti
        exe.setThreadNamePrefix("cursor-cli-exec-");
        exe.initialize();
        return exe;
    }
}
