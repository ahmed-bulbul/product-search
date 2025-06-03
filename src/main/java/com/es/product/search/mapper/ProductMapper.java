package com.es.product.search.mapper;

import com.es.product.search.dto.ProductDto;
import com.es.product.search.index.ProductIndex;
import com.es.product.search.models.Brand;
import com.es.product.search.models.Category;
import com.es.product.search.models.Product;
import com.es.product.search.models.ProductImage;
import com.es.product.search.utils.PageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    // --- DTO <-> Entity Mapping ---

    @Mapping(source = "brand", target = "brand", qualifiedByName = "mapBrandFromString")
    @Mapping(source = "category", target = "category", qualifiedByName = "mapCategoryFromString")
    @Mapping(source = "images", target = "images", qualifiedByName = "mapImagesToEntity")
    Product toEntity(ProductDto dto);

    @Mapping(source = "brand", target = "brand", qualifiedByName = "mapBrandToString")
    @Mapping(source = "category", target = "category", qualifiedByName = "mapCategoryToString")
    @Mapping(source = "images", target = "images", qualifiedByName = "mapImagesToUrls")
    ProductDto toDto(Product entity);

    List<ProductDto> toDtoList(List<Product> entities);

    List<ProductDto> toDtoListIndex(List<ProductIndex> indices);

    @Mapping(source = "brand", target = "brand", qualifiedByName = "mapBrandFromString")
    @Mapping(source = "category", target = "category", qualifiedByName = "mapCategoryFromString")
    @Mapping(source = "images", target = "images", qualifiedByName = "mapImagesToEntity")
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

    // --- Brand Mapping ---

    @Named("mapBrandFromString")
    default Brand mapBrandFromString(String brandName) {
        if (brandName == null || brandName.isEmpty()) return null;
        Brand brand = new Brand();
        brand.setName(brandName);
        return brand;
    }

    @Named("mapBrandToString")
    default String mapBrandToString(Brand brand) {
        return brand != null ? brand.getName() : null;
    }

    // --- Category Mapping ---

    @Named("mapCategoryFromString")
    default Category mapCategoryFromString(String categoryName) {
        if (categoryName == null || categoryName.isEmpty()) return null;
        Category category = new Category();
        category.setName(categoryName);
        return category;
    }

    @Named("mapCategoryToString")
    default String mapCategoryToString(Category category) {
        return category != null ? category.getName() : null;
    }

    // --- Images Mapping ---

    @Named("mapImagesToEntity")
    default Set<ProductImage> mapImagesToEntity(Set<String> urls) {
        if (urls == null) return Collections.emptySet();
        return urls.stream()
                .filter(Objects::nonNull)
                .map(url -> {
                    ProductImage image = new ProductImage();
                    image.setUrl(url);
                    return image;
                })
                .collect(Collectors.toSet());
    }

    @Named("mapImagesToUrls")
    default Set<String> mapImagesToUrls(Set<ProductImage> images) {
        if (images == null) return Collections.emptySet();
        return images.stream()
                .filter(Objects::nonNull)
                .map(ProductImage::getUrl)
                .collect(Collectors.toSet());
    }

    // --- Entity -> Index ---

    default ProductIndex fromEntity(Product product) {
        if (product == null) return null;

        List<String> imageUrls = Optional.ofNullable(product.getImages())
                .orElse(Collections.emptySet())
                .stream()
                .map(ProductImage::getUrl)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return new ProductIndex(
                product.getId(),
                Optional.ofNullable(product.getName()).orElse(""),
                Optional.ofNullable(product.getSku()).orElse(""),
                imageUrls,
                Optional.ofNullable(product.getDescription()).orElse(""),
                Optional.ofNullable(product.getRating()).orElse("0.0"),
                Optional.ofNullable(product.getPrice()).orElse(BigDecimal.ZERO),
                Optional.ofNullable(product.getQuantity()).orElse(0),
                Optional.ofNullable(product.getSize()).orElse(""),
                Optional.ofNullable(product.getColor()).orElse(""),
                Optional.ofNullable(product.getModel()).orElse(""),
                mapToIndexBrand(product.getBrand()),
                mapToIndexCategory(product.getCategory()),
                product.getCreatedAt() != null ? product.getCreatedAt().toString() : null,
                product.getUpdatedAt() != null ? product.getUpdatedAt().toString() : null
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

    // --- Index -> Entity (optional) ---

    default Product toEntity(ProductIndex index) {
        if (index == null) return null;

        Product product = new Product();
        product.setId(index.getId());
        product.setName(index.getName());
        product.setSku(index.getSku());
        product.setDescription(index.getDescription());
        product.setRating(index.getRating());
        product.setPrice(index.getPrice());
        product.setQuantity(index.getQuantity());
        product.setSize(index.getSize());
        product.setColor(index.getColor());
        product.setModel(index.getModel());

        if (index.getImages() != null) {
            Set<ProductImage> imageSet = index.getImages().stream()
                    .filter(Objects::nonNull)
                    .map(url -> {
                        ProductImage image = new ProductImage();
                        image.setUrl(url);
                        return image;
                    })
                    .collect(Collectors.toSet());
            product.setImages(imageSet);
        }

        if (index.getBrand() != null) {
            Brand brand = new Brand();
            brand.setName(index.getBrand().getName());
            product.setBrand(brand);
        }

        if (index.getCategory() != null) {
            Category category = new Category();
            category.setName(index.getCategory().getName());
            product.setCategory(category);
        }

        return product;
    }
}
