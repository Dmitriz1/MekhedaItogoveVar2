# Многопоточный парсер цен (Тестовое задание)

Сервис на Spring Boot для сбора цен с сайта локального магазина техники.

## Запуск

### Вариант 1: Запуск через IDE (IntelliJ IDEA / Giga IDE)
1. Откройте проект в IDE.
2. Найдите файл `src/main/java/ru.local.parser/ParserApplication.java`.
3. Нажмите на зеленую стрелочку (Run) рядом с методом `main`.
4. Дождитесь в консоли сообщения `Tomcat started on port 8080`.

### Вариант 2: Запуск через терминал
1. Откройте терминал в корневой папке проекта (где находится файл `pom.xml`).
2. Выполните команду:
   ```bash
   # Для Windows:
   .\mvnw spring-boot:run
   
   # Для Linux / macOS:
   ./mvnw spring-boot:run

## Реализация критериев
1. **Spring Boot с Unit тестами (6 б.)**:
   Проект написан на Spring Boot 3.2.5. В папке `src/test` есть тесты `ParserApplicationTests`, проверяющие контекст и логику сервиса (`@SpringBootTest`).
2. **Простые потоки Thread, Runnable (10 б.)**:
   Класс `SimpleThreadService.java`. Вызов эндпоинта `POST /api/parse-simple-thread` создает `new Thread(new Runnable())` для парсинга.
3. **Executor Service (Sheduler), блокирующие структуры, синхронизация, файл, демон-потоки (10 б.)**:
    - `SchedulerService.java`: написан собственный планировщик через `ScheduledExecutorService`.
    - `FileStorageService.java`: использует `LinkedBlockingQueue` (блокирующая очередь), `ReentrantLock` (примитив синхронизации) для записи в файл `products.txt`.
    - В этом же классе создается **демон-поток** (`setDaemon(true)`), который слушает очередь и асинхронно пишет в файл.
4. **Executor Service (Sheduler), блокирующие структуры, синхронизация, H2, Parallel Stream (14 б.)**:
    - H2 Database подключена через Spring Data JPA (`ProductRepository`), данные сохраняются в `PriceParserService`.
    - Шедулер (`SchedulerService`) раз в минуту проверяет данные.
    - Класс `PriceParserService` содержит метод `filterProductsInParallel()`, который достает все данные из H2 и фильтрует их по цене с помощью `parallelStream()`.

## Запрос

**Запуск парсинга каталога (с сохранением в H2 и файл):**
```bash
curl.exe -X POST http://localhost:8080/api/parse-catalog -H "Content-Type: application/json" -d "{\"urls\":[\"https://xn---72-9cd9bqh6aic.xn--p1ai/katalog/noutbuki-i-netbuki/\"]}"