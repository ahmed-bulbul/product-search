package com.es.product.search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.es.product.search.index.ProductIndex;
import com.es.product.search.respository.ProductIndexRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductIndexService {

    private final ElasticsearchClient elasticsearchClient;
    private final ProductIndexRepository productIndexRepository;

    public ProductIndex findById(UUID id) {
        return productIndexRepository.findById(id.toString()).orElse(null);
    }

    public void save(ProductIndex productIndex) {
        if (productIndex == null) return;
        productIndexRepository.save(productIndex);
        log.debug("Saved product to index: {}", productIndex.getId());
    }

    public void saveAll(List<ProductIndex> products) {
        if (products == null || products.isEmpty()) return;
        productIndexRepository.saveAll(products);
        log.debug("Saved {} products to index", products.size());
    }

    public void deleteById(UUID id) {
        if (id == null) return;
        productIndexRepository.deleteById(id.toString());
        log.debug("Deleted product from index: {}", id);
    }

    public long count() {
        return productIndexRepository.count();
    }

    public Page<ProductIndex> getAll(Pageable pageable) {
        return productIndexRepository.findAll(pageable);
    }

    public List<ProductIndex> searchByName(String name) throws IOException {
        if (name == null || name.isBlank()) {
            return Collections.emptyList();
        }

        int size = 10;

        SearchRequest searchRequest = SearchRequest.of(sr -> sr
                .index("products")
                .query(q -> q
                        .bool(b -> b
                                .should(sh -> sh.matchPhrasePrefix(mpp -> mpp.field("name").query(name)))
                                .should(sh -> sh.match(m -> m.field("name").query(name)))
                                .minimumShouldMatch("1")
                        )
                )
                .size(size)
        );

        log.info("Executing Elasticsearch search for name: {}", name);

        SearchResponse<ProductIndex> response = elasticsearchClient.search(searchRequest, ProductIndex.class);

        return response.hits().hits().stream()
                .map(Hit::source)
                .toList();
    }

    /**
     * Save or update the index.
     */
    public void saveOrUpdate(ProductIndex productIndex) {
        if (productIndex == null || productIndex.getId() == null) return;

        ProductIndex existing = findById(productIndex.getId());
        if (existing != null) {
            log.info("Updating existing index for ID: {}", productIndex.getId());
        } else {
            log.info("Creating new index for ID: {}", productIndex.getId());
        }

        save(productIndex);
    }
}
