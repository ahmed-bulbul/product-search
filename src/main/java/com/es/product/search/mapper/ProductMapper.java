package com.es.product.search.mapper;


import com.es.product.search.dto.ProductDto;
import com.es.product.search.models.Brand;
import com.es.product.search.models.Category;
import com.es.product.search.models.Product;
import com.es.product.search.utils.PageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "brand", target = "brand")
    @Mapping(source = "category", target = "category")
    Product toEntity(ProductDto dto);

    @Mapping(source = "brand.name", target = "brand")
    @Mapping(source = "category.name", target = "category")
    ProductDto toDto(Product entity);

    List<ProductDto> toDtoList(List<Product> entities);

    @Mapping(source = "brand", target = "brand")
    @Mapping(source = "category", target = "category")
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

    default Brand mapBrand(String brandName) {
        if (brandName == null) {
            return null;
        }
        Brand brand = new Brand();
        brand.setName(brandName);
        return brand;
    }

    default String mapBrand(Brand brand) {
        return brand != null ? brand.getName() : null;
    }

    default Category mapCategory(String categoryName) {
        if (categoryName == null) {
            return null;
        }
        Category category = new Category();
        category.setName(categoryName);
        return category;
    }

    default String mapCategory(Category category) {
        return category != null ? category.getName() : null;
    }
}
