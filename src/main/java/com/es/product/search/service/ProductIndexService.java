package com.es.product.search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.es.product.search.index.ProductIndex;
import com.es.product.search.respository.ProductIndexRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

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

    public Page<ProductIndex> searchByName(Map<String, String> filters, int page, int size) throws IOException {
        String name = filters.get("name");
        String categoryNames = filters.get("categoryNames");
        List<String> categories = categoryNames != null
                ? Arrays.stream(categoryNames.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList()
                : List.of();


        SearchRequest searchRequest = SearchRequest.of(sr -> sr
                .index("products")
                .from(page * size)
                .size(size)
                .query(q -> q.bool(b -> {
                    boolean hasName = name != null && !name.isBlank();
                    boolean hasCategories = categories != null && !categories.isEmpty();

                    // Match product name
                    if (hasName) {
                        b.should(s -> s.matchPhrasePrefix(mpp -> mpp.field("name").query(name)));
                        b.should(s -> s.match(m -> m.field("name").query(name)));
                    }

                    // Match any category name (nested query)
                    if (hasCategories) {
                        for (String cat : categories) {
                            b.should(s -> s.nested(n -> n
                                    .path("category")
                                    .query(nq -> nq.match(m -> m
                                            .field("category.name")
                                            .query(cat)
                                    ))
                            ));
                        }
                    }

                    // If we have should clauses, set minimum_should_match = 1
                    if (hasName || hasCategories) {
                        b.minimumShouldMatch("1");
                    }

                    return b;
                }))
        );



        log.info("Search request: {}", searchRequest);

        // Execute query
        SearchResponse<ProductIndex> response = elasticsearchClient.search(searchRequest, ProductIndex.class);

        List<ProductIndex> content = response.hits().hits().stream()
                .map(Hit::source)
                .toList();

        long totalHits = response.hits().total().value();
        Pageable pageable = PageRequest.of(page, size);

        return new PageImpl<>(content, pageable, totalHits);
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

    public Page<ProductIndex> findByCategory(String category,Pageable pageable) {
        return productIndexRepository.findByCategory_Name(category,pageable);
    }
}
