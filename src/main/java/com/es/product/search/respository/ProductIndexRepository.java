package com.es.product.search.respository;

import com.es.product.search.index.ProductIndex;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductIndexRepository extends ElasticsearchRepository<ProductIndex, String> {
    List<ProductIndex> findByNameContainingIgnoreCase(String name);

    Page<ProductIndex> findByCategory_Name(String category, Pageable pageable);
}
