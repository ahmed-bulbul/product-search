package com.es.product.search.config;

import com.es.product.search.model.Product;
import com.es.product.search.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DataLoader implements ApplicationRunner {

    private final ProductService productService;

    // Brands and product types to generate meaningful names
    private final List<String> brands = List.of(
            "Apple", "Samsung", "Google", "OnePlus", "Sony", "LG", "Huawei", "Xiaomi", "Nokia", "Motorola"
    );

    private final List<String> productTypes = List.of(
            "iPhone", "Galaxy", "Pixel", "Nord", "Xperia", "G", "Mate", "Mi", "Lumia", "Edge"
    );

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (productService.count() > 0) {
            System.out.println("Products already loaded, skipping data initialization.");
            return;
        }

        List<Product> products = new ArrayList<>();

        int productCount = 0;
        for (String brandName : brands) {
            for (String type : productTypes) {
                for (int i = 1; i <= 10; i++) {  // 10 * 10 * 10 = 1000 products
                    if (productCount >= 1000) break;

                    Product.Brand brand = new Product.Brand(
                            "brand" + (brands.indexOf(brandName) + 1),
                            brandName
                    );

                    Product p = new Product();
                    p.setId(UUID.randomUUID().toString());
                    p.setSku("sku-" + (productCount + 1));
                    p.setImageUrl("https://via.placeholder.com/150");
                    p.setName(brandName + " " + type + " " + i);
                    p.setDescription("Description for " + brandName + " " + type + " " + i);
                    p.setRating((float) (Math.random() * 5));
                    p.setPrice(Math.random() * 1000);
                    p.setBrand(brand);

                    products.add(p);
                    productCount++;
                }
                if (productCount >= 1000) break;
            }
            if (productCount >= 1000) break;
        }

        productService.saveAll(products);
        System.out.println("Inserted " + productCount + " meaningful sample products.");
    }
}
