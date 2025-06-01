package com.es.product.search.dto;


import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {
    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private String sku;
    private Integer quantity;
    private String size;
    private String color;
    private String model;
    private String brand;      // brand name
    private String category;   // category name
    private Instant createdAt;
    private Instant updatedAt;
    private boolean active;
}
