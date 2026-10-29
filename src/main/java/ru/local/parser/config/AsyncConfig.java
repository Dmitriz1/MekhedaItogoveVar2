package ru.local.parser.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class AsyncConfig {
    @Bean(name = "parserExecutor", destroyMethod = "shutdown")
    public ExecutorService parserExecutor(@Value("${parser.threads}") int threads) {
        return new ThreadPoolExecutor(threads, threads, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), r -> {
            Thread t = new Thread(r, "parser-" + System.nanoTime());
            t.setDaemon(true);
            return t;
        });
    }

    @Bean
    public RestTemplate restTemplate(@Value("${parser.user-agent}") String userAgent) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("User-Agent", userAgent);
            return execution.execute(request, body);
        });
        return restTemplate;
    }
}