package ru.local.parser.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerService {
    private final PriceParserService parserService;

    @PostConstruct
    public void startScheduler() {
        // Критерий 3 и 4: cвой Sheduler через ScheduledExecutorService
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(() -> {
            log.info("Запуск плановой проверки цен");
            try {
                // Пример: раз в минуту фильтруем товары дороже 30000
                var filtered = parserService.filterProductsInParallel(3000L);
                log.info("Найдено премиум-товаров: {}", filtered.size());
            } catch (Exception e) {
                log.error("Ошибка", e);
            }
        }, 10, 60, TimeUnit.SECONDS); // старт через 10 сек, потом каждые 60 сек
    }
}