package ru.local.parser.parser;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;
import ru.local.parser.config.ParserProperties;
import ru.local.parser.dto.ProductDto;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JsoupProductParser {
    private final ParserProperties props;

    public List<String> extractProductLinks(String catalogUrl) throws IOException {
        Document doc = Jsoup.connect(catalogUrl)
                .userAgent(props.getUserAgent())
                .timeout(props.getTimeoutMs())
                .get();

        return doc.select(props.getSelectors().getProductCardLink()).stream()
                .map(a -> a.absUrl("href"))
                .filter(href -> !href.isEmpty())
                .distinct()
                .toList();
    }

    public ProductDto parseProduct(String url) throws IOException {
        Document doc = Jsoup.connect(url).userAgent(props.getUserAgent()).timeout(props.getTimeoutMs()).get();

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