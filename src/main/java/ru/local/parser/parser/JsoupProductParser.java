package ru.local.parser.parser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.local.parser.config.ParserProperties;
import ru.local.parser.dto.ProductDto;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JsoupProductParser {
    private final ParserProperties props;
    private final RestTemplate restTemplate;

    public List<String> extractProductLinks(String catalogUrl) {
        // Скачиваем HTML через RestTemplate
        String html = restTemplate.getForObject(catalogUrl, String.class);

        // Парсим скачанный HTML через Jsoup
        Document doc = Jsoup.parse(html != null ? html : "", catalogUrl);

        return doc.select(props.getSelectors().getProductCardLink()).stream()
                .map(a -> a.absUrl("href"))
                .filter(href -> !href.isEmpty())
                .distinct()
                .toList();
    }

    public ProductDto parseProduct(String url) {
        String html = restTemplate.getForObject(url, String.class);
        Document doc = Jsoup.parse(html != null ? html : "", url);

        String title = text(doc, props.getSelectors().getTitle());
        Long price = extractPrice(doc);

        log.info("Спарсено: {} | Цена: {}", title, price);

        String desc = text(doc, props.getSelectors().getDescription());
        String date = text(doc, props.getSelectors().getDate());

        return ProductDto.builder()
                .url(url).title(title).price(price)
                .description(desc).publishedDate(date).build();
    }

    private String text(Document doc, String selector) {
        Element el = doc.selectFirst(selector);
        return el == null ? null : el.text().trim();
    }

    private Long extractPrice(Document doc) {
        Element panelEl = doc.selectFirst(props.getSelectors().getPriceAttr());
        if (panelEl != null) {
            String priceAttr = panelEl.attr("data-compare-price");
            if (!priceAttr.isEmpty()) {
                try { return Long.parseLong(priceAttr); } catch (Exception ignored) {}
            }
        }

        Element textEl = doc.selectFirst(props.getSelectors().getPriceText());
        if (textEl != null) {
            String cleanPrice = textEl.text().replaceAll("[^0-9]", "");
            if (!cleanPrice.isEmpty()) {
                try { return Long.parseLong(cleanPrice); } catch (Exception ignored) {}
            }
        }
        return null;
    }
}