package ru.local.parser;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.local.parser.service.PriceParserService;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ParserApplicationTests {

    @Autowired
    private PriceParserService service;

    @Test
    void contextLoads() {
        // Проверка, что контекст Spring поднимается
        assertNotNull(service);
    }

    @Test
    void testParallelFilterReturnsResult() {
        // Проверка работы метода фильтрации (вызов не должен падать)
        var result = service.filterProductsInParallel(10000L);
        assertNotNull(result);
    }
}