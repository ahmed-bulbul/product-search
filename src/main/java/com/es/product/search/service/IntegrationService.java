package com.es.product.search.service;


import com.es.product.search.index.ProductIndex;
import com.es.product.search.mapper.ProductMapper;
import com.es.product.search.models.Product;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class IntegrationService {
    private final ProductIndexService productIndexService;
    private final ProductService productService;
    private final ProductMapper productMapper; // You should define this bean

    @Transactional
    public void indexProducts() {
        int page = 0;
        int size = 10;

        while (true) {
            Page<Product> products = productService.findAllPaginated(page, size);
            if (!products.hasContent()) break;

            List<Product> content = products.getContent();
            content.forEach(product -> {
                if (product == null) return;

                ProductIndex existingIndex = productIndexService.findById(product.getId());

                if (existingIndex != null) {
                    if (existingIndex.getPrice() != null &&
                            existingIndex.getPrice().compareTo(product.getPrice()) != 0) {

                        log.info("Updating index for product ID: {}", product.getId());
                        ProductIndex updatedIndex = productMapper.fromEntity(product);
                        productIndexService.save(updatedIndex);
                    }
                }
                else {
                    log.info("Creating index for product ID: {}", product.getId());
                    ProductIndex newIndex = productMapper.fromEntity(product);
                    productIndexService.save(newIndex);
                }
            });

            if (!products.hasNext()) break;
            page++;
        }
    }
}
