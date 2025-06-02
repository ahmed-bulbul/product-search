package com.es.product.search.service;


import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public interface BaseService<T> {
    T save(T entity);
    T findById(UUID id);
    List<T> findAll(Predicate<T> filter);
    Page<T> findAllPaginated(int page, int size);
    void update(UUID id, Consumer<T> updater);
    List<String> mapAll(Function<T, String> mapper);
    void delete(UUID id);
}

