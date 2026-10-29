package ru.local.parser.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ru.local.parser.repository.ProductRepository;
import org.springframework.web.bind.annotation.*;
import ru.local.parser.dto.ParseRequest;
import ru.local.parser.dto.ProductDto;
import ru.local.parser.entity.Product;
import ru.local.parser.service.PriceParserService;
import ru.local.parser.service.SimpleThreadService;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ParseController {
    private final ProductRepository repository;
    private final SimpleThreadService simpleThreadService;
    private final PriceParserService priceParserService;

    @PostMapping("/parse-catalog")
    public List<ProductDto> parseCatalog(@RequestBody @Valid ParseRequest req) {
        return priceParserService.parseCatalog(req.getUrls().get(0));
    }

    // Демонстрация Критерия 2 (Простые потоки)
    @PostMapping("/parse-simple-thread")
    public String parseSimple(@RequestBody @Valid ParseRequest req) {
        simpleThreadService.parseWithSimpleThreads(req.getUrls().get(0));
        return "Запущен простой поток (Runnable) для парсинга";
    }

    // Демонстрация Критерия 4 (Parallel Stream + H2)
    @GetMapping("/answer/filter")
    public List<Product> filter(
            @RequestParam Long minPrice,
            @RequestParam(defaultValue = "false") boolean sortDesc) {
        return priceParserService.filterProductsInParallel(minPrice, sortDesc);
    }

    @GetMapping({"/products", "/answer"})
    public List<Product> all() {
        return repository.findAll();
    }
}