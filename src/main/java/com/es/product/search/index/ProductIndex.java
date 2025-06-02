package com.es.product.search.index;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
// ProductIndex.java snippet correction:
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "products")
@JsonIgnoreProperties(ignoreUnknown = true)
@Setting(settingPath = "/elasticsearch/product-settings.json")
public class ProductIndex {

    @Id
    private UUID id;

    @Field(type = FieldType.Text, analyzer = "ngram_analyzer", searchAnalyzer = "autocomplete_search")
    private String name;

    @Field(type = FieldType.Keyword)
    private String sku;

    @Field(type = FieldType.Keyword)
    private List<String> images;

    @Field(type = FieldType.Text)
    private String description;

    // Changed from String to Double to match @Field(type = FieldType.Double)
    @Field(type = FieldType.Text)
    private String rating;

    @Field(type = FieldType.Double)
    private BigDecimal price; // Use Double here for Elasticsearch compatibility

    @Field(type = FieldType.Integer)
    private Integer quantity;

    @Field(type = FieldType.Keyword)
    private String size;

    @Field(type = FieldType.Keyword)
    private String color;

    @Field(type = FieldType.Keyword)
    private String model;

    @Field(type = FieldType.Nested)
    private Brand brand;

    @Field(type = FieldType.Nested)
    private Category category;

    @Field(type = FieldType.Text)
    private String createdAt;

    @Field(type = FieldType.Text)
    private String updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Brand {
        @Field(type = FieldType.Keyword)
        private String id;

        @Field(type = FieldType.Keyword)
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Category {
        @Field(type = FieldType.Keyword)
        private String id;

        @Field(type = FieldType.Keyword)
        private String name;
    }
}
