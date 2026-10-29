## Как запустить проект
1. Откройте проект в IntelliJ IDEA
2. Запустите главный класс `ParserApplication`
3. Приложение запустится на порту 8080. База данных H2 создается автоматически в памяти.

## Как проверить логику работы
**Шаг 1. Запуск парсинга каталога**
Выполните POST-запрос на полный URL в PowerShell:  
curl.exe -X POST http://localhost:8080/api/parse-catalog -H "Content-Type: application/json" -d "{\"urls\":[\"https://xn---72-9cd9bqh6aic.xn--p1ai/katalog/noutbuki-i-netbuki/\"]}"

### Получение и проверка результатов
После того как парсер отработал, все данные сохраняются в базу H2.
Чтобы получить их, откройте в браузере ссылки для получения результатов:
👉 **`http://localhost:8080/api/products`** (или `http://localhost:8080/api/answer`)

```json
[
  {
    "id": 6,
    "url": "https://xn---72-9cd9bqh6aic.xn--p1ai/katalog/noutbuki-i-netbuki/asus-vivobook-pro-16x-oled-k6604jv/",
    "title": "ASUS Vivobook Pro 16X OLED K6604JV",
    "price": 90000,
    "description": null,
    "publishedDate": null,
    "parsedAt": "2026-10-29T14:19:42.490295"
  },
  {
    "id": 28,
    "url": "https://xn---72-9cd9bqh6aic.xn--p1ai/katalog/noutbuki-i-netbuki/acer-nitro-v15-anv15-41-r99v/",
    "title": "Ноутбук Acer Nitro V15 ANV15-41-R99V",
    "price": 80000,
    "description": null,
    "publishedDate": null,
    "parsedAt": "2026-10-29T14:19:42.557273"
  },
  {
    "id": 13,
    "url": "https://xn---72-9cd9bqh6aic.xn--p1ai/katalog/noutbuki-i-netbuki/apple-macbook-air-13-inch-2020-a2179/",
    "title": "Ноутбук Apple MacBook Air 13-inch 2020 (A2179)",
    "price": 30000,
    "description": null,
    "publishedDate": null,
    "parsedAt": "2026-10-29T14:19:42.511294"
  }
]