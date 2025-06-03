package com.es.product.search.controller;


import com.es.product.search.dto.ProductDto;
import com.es.product.search.index.ProductIndex;
import com.es.product.search.mapper.ProductMapper;
import com.es.product.search.service.ProductIndexService;
import com.es.product.search.utils.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/productIndex")
public class ProductIndexController {

    private final ProductIndexService service;
    private final ProductMapper mapper;

    public ProductIndexController(ProductIndexService productIndexService, ProductMapper mapper) {
        this.service = productIndexService;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<PageResponse<ProductDto>> getAll(@RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size) {

        var paged = service.getAll(PageRequest.of(page, size));
        return ResponseEntity.ok(mapper.toPageResponseIndex(paged));
    }

    @GetMapping("/search")
    public List<ProductIndex> search(@RequestParam String name) throws IOException {
        return service.searchByName(name);
    }

    @GetMapping("/{id}")
    public ProductIndex getById(@PathVariable String id) {
        return service.findById(UUID.fromString(id));
    }

    @GetMapping("/category")
    ResponseEntity<PageResponse<ProductDto>>  searchByCategory(
            @RequestParam String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductIndex> paged = service.findByCategory(category, pageable);
        return ResponseEntity.ok(mapper.toPageResponseIndex(paged));
    }


}
