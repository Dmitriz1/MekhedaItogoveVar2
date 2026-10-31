package ru.local.parser.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.local.parser.parser.JsoupProductParser;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimpleThreadService {
    private final JsoupProductParser parser;

    // Использование простых потоков (Thread, Runnable)
    public void parseWithSimpleThreads(String url) {
        Runnable task = () -> {
            log.info("[Простой поток] Начал парсинг: {}", url);
            try {
                parser.parseProduct(url);
                log.info("[Простой поток] Завершил парсинг: {}", url);
            } catch (Exception e) {
                log.error("[Простой поток] Ошибка: {}", e.getMessage());
            }
        };

        Thread thread = new Thread(task);
        thread.start();
    }
}