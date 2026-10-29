package ru.local.parser.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.local.parser.entity.Product;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByUrl(String url);
}