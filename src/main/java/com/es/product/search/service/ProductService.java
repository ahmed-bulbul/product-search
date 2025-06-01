package com.es.product.search.service;


import com.es.product.search.dto.ProductDto;
import com.es.product.search.models.Brand;
import com.es.product.search.models.Category;
import com.es.product.search.models.Product;
import com.es.product.search.respository.BrandRepository;
import com.es.product.search.respository.CategoryRepository;
import com.es.product.search.respository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class ProductService implements BaseService<Product> {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public Product save(Product entity) {
        // Brand lookup
        Brand brand = brandRepository.findByName(entity.getBrand().getName())
                .orElseThrow(() -> new IllegalArgumentException("Brand not found: " + entity.getBrand().getName()));
        entity.setBrand(brand);

        // Category lookup
        Category category = categoryRepository.findByName(entity.getCategory().getName())
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + entity.getCategory().getName()));
        entity.setCategory(category);

        return productRepository.save(entity);
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return productRepository.findById(id);
    }

    @Override
    public List<Product> findAll(Predicate<Product> filter) {
        return productRepository.findAll().stream().filter(filter).toList();
    }

    @Override
    public Page<Product> findAllPaginated(int page, int size) {
        return productRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        );
    }

    @Override
    public void update(UUID id, Consumer<Product> updater) {
        productRepository.findById(id).ifPresent(product -> {
            updater.accept(product);
            productRepository.save(product);
        });
    }

    @Override
    public List<String> mapAll(Function<Product, String> mapper) {
        return productRepository.findAll().stream().map(mapper).toList();
    }

    @Override
    public void delete(UUID id) {
        productRepository.deleteById(id);
    }
}
