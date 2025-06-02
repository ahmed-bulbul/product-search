package com.es.product.search.controller;


import com.es.product.search.dto.ProductDto;
import com.es.product.search.mapper.ProductMapper;
import com.es.product.search.service.ProductService;
import com.es.product.search.utils.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductMapper mapper;

    @PostMapping
    public ResponseEntity<ProductDto> create(@RequestBody ProductDto dto) {
        var saved = productService.save(mapper.toEntity(dto));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapper.toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable UUID id, @RequestBody ProductDto dto) {
        productService.update(id, product -> mapper.updateEntityFromDto(dto, product));
        return ResponseEntity.noContent().build(); // 204 No Content
    }


    @GetMapping
    public ResponseEntity<PageResponse<ProductDto>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size
    ) {
        var paged = productService.findAllPaginated(page, size);
        return ResponseEntity.ok(mapper.toPageResponse(paged));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        productService.delete(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
