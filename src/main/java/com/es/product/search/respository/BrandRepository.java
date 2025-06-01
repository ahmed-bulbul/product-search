package com.es.product.search.respository;

import com.es.product.search.index.ProductIndex;
import com.es.product.search.models.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BrandRepository extends JpaRepository<Brand, UUID> {
    Optional<Brand> findByName(String name);

}
