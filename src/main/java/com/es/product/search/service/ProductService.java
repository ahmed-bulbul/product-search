package com.es.product.search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.es.product.search.model.Product;
import com.es.product.search.respository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ElasticsearchClient elasticsearchClient;

    private final ProductRepository productRepository;


    public List<Product> searchByName(String name) throws IOException {
        if (name == null || name.isBlank()) {
            return Collections.emptyList();
        }

        int size = 10; // max 10 results

        SearchRequest searchRequest = SearchRequest.of(sr -> sr
                .index("products")
                .query(q -> q
                        .bool(b -> b
                                .should(sh -> sh
                                        .matchPhrasePrefix(mpp -> mpp
                                                .field("name")
                                                .query(name)
                                        )
                                )
                                .should(sh -> sh
                                        .match(m -> m
                                                .field("name")
                                                .query(name)
                                        )
                                )
                                .minimumShouldMatch("1")
                        )
                )
                .size(size)
        );



        // Consider using a logger here instead of System.out.println
        System.out.println("Elasticsearch search request: " + searchRequest);

        SearchResponse<Product> response = elasticsearchClient.search(searchRequest, Product.class);

        return response.hits().hits().stream()
                .map(Hit::source)
                .toList();
    }

    public Page<Product> getAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public void saveAll(List<Product> products) {
        productRepository.saveAll(products);
    }

    public long count() {
        return productRepository.count();
    }

}
