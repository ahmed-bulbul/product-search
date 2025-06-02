package com.es.product.search.mapper;

import com.es.product.search.dto.ProductDto;
import com.es.product.search.index.ProductIndex;
import com.es.product.search.models.Brand;
import com.es.product.search.models.Category;
import com.es.product.search.models.Product;
import com.es.product.search.utils.PageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    // --- DTO <-> Entity Mapping ---

    @Mapping(source = "brand", target = "brand", qualifiedByName = "mapBrandFromString")
    @Mapping(source = "category", target = "category", qualifiedByName = "mapCategoryFromString")
    Product toEntity(ProductDto dto);

    @Mapping(source = "brand", target = "brand", qualifiedByName = "mapBrandToString")
    @Mapping(source = "category", target = "category", qualifiedByName = "mapCategoryToString")
    ProductDto toDto(Product entity);

    List<ProductDto> toDtoList(List<Product> entities);

    List<ProductDto> toDtoListIndex(List<ProductIndex> entities);

    @Mapping(source = "brand", target = "brand", qualifiedByName = "mapBrandFromString")
    @Mapping(source = "category", target = "category", qualifiedByName = "mapCategoryFromString")
    void updateEntityFromDto(ProductDto dto, @MappingTarget Product entity);

    default PageResponse<ProductDto> toPageResponse(Page<Product> page) {
        return new PageResponse<>(
                toDtoList(page.getContent()),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    default PageResponse<ProductDto> toPageResponseIndex(Page<ProductIndex> page) {
        return new PageResponse<>(
                toDtoListIndex(page.getContent()),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    // --- Custom mapping methods ---

    @Named("mapBrandFromString")
    default Brand mapBrandFromString(String brandName) {
        if (brandName == null) return null;
        Brand brand = new Brand();
        brand.setName(brandName);
        return brand;
    }

    @Named("mapBrandToString")
    default String mapBrandToString(Brand brand) {
        return brand != null ? brand.getName() : null;
    }

    @Named("mapCategoryFromString")
    default Category mapCategoryFromString(String categoryName) {
        if (categoryName == null) return null;
        Category category = new Category();
        category.setName(categoryName);
        return category;
    }

    @Named("mapCategoryToString")
    default String mapCategoryToString(Category category) {
        return category != null ? category.getName() : null;
    }

    // --- Product -> ProductIndex for Elasticsearch ---

    default ProductIndex fromEntity(Product product) {
        if (product == null) return null;

        return new ProductIndex(
                product.getId(),
                product.getName() != null ? product.getName() : "",
                product.getSku() != null ? product.getSku() : "",
                product.getImages() != null ? new ArrayList<>(product.getImages()) : new ArrayList<>(),
                product.getDescription() != null ? product.getDescription() : "",
                product.getRating() != null ? product.getRating() : "0.0",
                product.getPrice() != null ? product.getPrice() : BigDecimal.ZERO,
                product.getQuantity() != null ? product.getQuantity() : 0,
                product.getSize() != null ? product.getSize() : "",
                product.getColor() != null ? product.getColor() : "",
                product.getModel() != null ? product.getModel() : "",
                mapToIndexBrand(product.getBrand()),
                mapToIndexCategory(product.getCategory()),
                product.getCreatedAt().toString(),
                product.getUpdatedAt().toString()
        );
    }


    default ProductIndex.Brand mapToIndexBrand(Brand brand) {
        if (brand == null) return null;
        return new ProductIndex.Brand(
                brand.getId() != null ? brand.getId().toString() : null,
                brand.getName()
        );
    }

    default ProductIndex.Category mapToIndexCategory(Category category) {
        if (category == null) return null;
        return new ProductIndex.Category(
                category.getId() != null ? category.getId().toString() : null,
                category.getName()
        );
    }

    default Double safeParseDouble(String rating) {
        try {
            return rating != null ? Double.parseDouble(rating) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // --- Optional: reverse from ProductIndex to Product ---

    default Product toEntity(ProductIndex index) {
        if (index == null) return null;

        Product product = new Product();
        product.setId(index.getId());
        product.setName(index.getName());
        product.setSku(index.getSku());
        product.setImages(Set.copyOf(index.getImages() != null ? index.getImages() : new ArrayList<>()));
        product.setDescription(index.getDescription());
        product.setRating(index.getRating() != null ? String.valueOf(index.getRating()) : null);
        product.setPrice(index.getPrice());
        product.setQuantity(index.getQuantity());
        product.setSize(index.getSize());
        product.setColor(index.getColor());
        product.setModel(index.getModel());
        return product;
    }
}
