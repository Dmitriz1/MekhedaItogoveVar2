package ru.local.parser.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.local.parser.config.ParserProperties;
import ru.local.parser.dto.ProductDto;
import ru.local.parser.entity.Product;
import ru.local.parser.parser.JsoupProductParser;
import ru.local.parser.repository.ProductRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PriceParserService {
    private final JsoupProductParser parser;
    private final ProductRepository repository;
    private final ParserProperties props;
    private final FileStorageService fileStorage;

    @Qualifier("parserExecutor")
    private final ExecutorService executor;

    public List<ProductDto> parseCatalog(String catalogUrl) {
        log.info("=== [API] Старт парсинга каталога ===");
        try {
            List<String> urls = parser.extractProductLinks(catalogUrl);
            log.info("=== [API] Найдено ссылок на товары: {} ===", urls.size());

            if (urls.isEmpty()) return List.of();

            List<CompletableFuture<ProductDto>> futures = urls.stream()
                    .map(url -> CompletableFuture.supplyAsync(() -> safeParse(url), executor))
                    .toList();

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            return futures.stream()
                    .map(CompletableFuture::join)
                    .peek(dto -> {
                        if (dto.getError() == null) {
                            fileStorage.saveToFileAsync(dto);
                            if (repository.findByUrl(dto.getUrl()).isEmpty()) {
                                repository.save(dto.toEntity());
                            }
                        }
                    })
                    .collect(Collectors.toList());

        } catch (IOException e) {
            log.error("=== [API] Ошибка: {} ===", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private ProductDto safeParse(String url) {
        try {
            Thread.sleep(props.getPoliteDelayMs());
            return parser.parseProduct(url);
        } catch (Exception e) {
            return ProductDto.builder().url(url).error(e.getMessage()).build();
        }
    }

    // Фильтрация с использованием Parallel Stream
    public List<Product> filterProductsInParallel(Long minPrice) {
        List<Product> allProducts = repository.findAll();

        log.info("Запуск Parallel Stream фильтрации для {} товаров", allProducts.size());
        return allProducts.parallelStream()
                .filter(p -> p.getPrice() != null && p.getPrice() >= minPrice)
                .collect(Collectors.toList());
    }
}