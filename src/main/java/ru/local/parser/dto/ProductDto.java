package ru.local.parser.dto;

import lombok.*;
import ru.local.parser.entity.Product;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ProductDto {
    private String url;
    private String title;
    private Long price;
    private String description;
    private String publishedDate;
    private String error;

    public Product toEntity() {
        return Product.builder()
                .url(url).title(title).price(price)
                .description(description).publishedDate(publishedDate)
                .parsedAt(LocalDateTime.now()).build();
    }
}