package com.es.product.search.models;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "product_images")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImage extends BaseEntity{

    private String url;
    private String description;
    @ManyToOne
    @JoinColumn(name = "product_id")  // This is the owning side
    private Product product;
}
