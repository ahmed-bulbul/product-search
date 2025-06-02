package com.es.product.search.models;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product extends BaseEntity {

    private String name;
    private String description;
    private BigDecimal price;
    private String sku;
    private Integer quantity;
    private String size;
    private String color;
    private String model;
    private String rating;
    private Set<String> images = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    public static Supplier<Product> defaultSupplier() {
        return () -> Product.builder()
                .name("Default Product")
                .description("Default Description")
                .price(BigDecimal.ZERO)
                .sku("SKU-000")
                .quantity(0)
                .brand(Brand.builder().name("Default Brand").build())
                .category(Category.builder().name("Default Category").build())
                .build();
    }
}

