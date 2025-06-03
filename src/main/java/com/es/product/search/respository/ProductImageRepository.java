package com.es.product.search.respository;

import com.es.product.search.models.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductImageRepository extends JpaRepository<ProductImage, UUID> {
}
