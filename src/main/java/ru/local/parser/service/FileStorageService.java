package ru.local.parser.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.local.parser.dto.ProductDto;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
public class FileStorageService {
    // Блокирующая структура
    private final BlockingQueue<ProductDto> queue = new LinkedBlockingQueue<>();
    // Примитив синхронизации
    private final ReentrantLock lock = new ReentrantLock();
    private final String FILE_NAME = "products.txt";

    @PostConstruct
    public void init() {
        // Демон-поток для записи в файл
        Thread writerThread = new Thread(() -> {
            while (true) {
                try {
                    ProductDto product = queue.take(); // Блокируется, если очередь пуста
                    writeToFile(product);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        writerThread.setDaemon(true);
        writerThread.start();
    }

    public void saveToFileAsync(ProductDto product) {
        queue.offer(product);
    }

    private void writeToFile(ProductDto product) {
        lock.lock();
        try (FileWriter fw = new FileWriter(FILE_NAME, true)) {
            fw.write(product.getTitle() + " | " + product.getPrice() + " | " + product.getUrl() + "\n");
        } catch (IOException e) {
            log.error("Ошибка записи в файл", e);
        } finally {
            lock.unlock();
        }
    }
}