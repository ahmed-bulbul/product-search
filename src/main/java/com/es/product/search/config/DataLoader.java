package com.es.product.search.config;

import com.es.product.search.index.ProductIndex;
import com.es.product.search.models.Brand;
import com.es.product.search.models.Category;
import com.es.product.search.models.Product;
import com.es.product.search.models.ProductImage;
import com.es.product.search.respository.BrandRepository;
import com.es.product.search.respository.CategoryRepository;
import com.es.product.search.respository.ProductImageRepository;
import com.es.product.search.respository.ProductRepository;
import com.es.product.search.service.ProductIndexService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Component
@RequiredArgsConstructor
public class DataLoader implements ApplicationRunner {

    private final ProductIndexService productIndexService;


    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;

    private final Random random = new Random();

    private static final List<String> BRANDS = List.of(
            "Apple", "Samsung", "Sony", "Dell", "HP",
            "Lenovo", "Asus", "Nike", "Adidas", "Puma"
    );

    private static final List<String> CATEGORIES = List.of(
            "Smartphones", "Laptops", "Headphones", "Shoes", "Watches",
            "Cameras", "Tablets", "Monitors", "Printers", "Backpacks"
    );

    private static final List<String> PRODUCT_PREFIXES = List.of(
            "Pro", "Max", "Ultra", "Mini", "Air", "Plus", "Smart", "Elite", "Sport", "X"
    );

    private static final List<String> MODEL_NAMES = List.of(
            "One", "S", "Z", "G5", "M2", "X1", "9i", "Edge", "Nova", "Flex"
    );



    private void loadDemoData() {
        // 1. Save brands
        List<Brand> brandEntities = new ArrayList<>();
        for (String name : BRANDS) {
            brandEntities.add(Brand.builder()
                    .name(name)
                    .imageUrl("https://dummyimage.com/100x100/000/fff&text=" + name.charAt(0))
                    .description(name + " official brand")
                    .build());
        }
        brandRepository.saveAll(brandEntities);

        // 2. Save categories
        List<Category> categoryEntities = new ArrayList<>();
        for (String name : CATEGORIES) {
            Category cat = new Category();
            cat.setName(name);
            cat.setDescription("Products under " + name);
            categoryEntities.add(cat);
        }
        categoryRepository.saveAll(categoryEntities);

        // 3. Save 1000 products
        List<Product> products = new ArrayList<>();

        for (int i = 1; i <= 1000; i++) {
            Brand brand = brandEntities.get(random.nextInt(brandEntities.size()));
            Category category = categoryEntities.get(random.nextInt(categoryEntities.size()));
            String prefix = PRODUCT_PREFIXES.get(random.nextInt(PRODUCT_PREFIXES.size()));
            String model = MODEL_NAMES.get(random.nextInt(MODEL_NAMES.size()));
            String productName = brand.getName() + " " + prefix + " " + model;

            Product product = Product.builder()
                    .name(productName)
                    .description("High-quality " + category.getName().toLowerCase() + " from " + brand.getName())
                    .sku("SKU-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                    .price(BigDecimal.valueOf(random.nextInt(2000) + 100))
                    .brand(brand)
                    .quantity(random.nextInt(100))
                    .model(model)
                    .color(String.valueOf(random.nextInt(100) + 1))
                    .size(String.valueOf(random.nextInt(100) + 1))
                    .category(category)
                    .build();

            ProductImage productImage = ProductImage.builder()
                    .url("https://images.pexels.com/photos/90946/pexels-photo-90946.jpeg")
                    .description(productName)
                    .product(product)
                    .build();

            product.getImages().add(productImage);

            products.add(product);
        }

        productRepository.saveAll(products);

        System.out.println("✅ Loaded: 10 Brands, 10 Categories, 1000 Products.");
    }

    // Brands and product types to generate meaningful names
    private final List<String> brands = List.of(
            "Apple", "Samsung", "Google", "OnePlus", "Sony", "LG", "Huawei", "Xiaomi", "Nokia", "Motorola"
    );

    private final List<String> productTypes = List.of(
            "iPhone", "Galaxy", "Pixel", "Nord", "Xperia", "G", "Mate", "Mi", "Lumia", "Edge"
    );

    @Override
    public void run(ApplicationArguments args) throws Exception {

//        if(1==1){
//            productRepository.deleteAll();
//            brandRepository.deleteAll();
//            categoryRepository.deleteAll();
//            return;
//        }

        if (productRepository.count() == 0) {
            System.out.println("No products found, loading demo data.");
            loadDemoData();
        }else{
//            //delete all products
//            productRepository.deleteAll();
//            brandRepository.deleteAll();
//            categoryRepository.deleteAll();
        }

        if (productIndexService.count() > 0) {
            System.out.println("Products already loaded, skipping data initialization.");
            return;
        }



//        List<ProductIndex> products = new ArrayList<>();
//
//        int productCount = 0;
//        for (String brandName : brands) {
//            for (String type : productTypes) {
//                for (int i = 1; i <= 10; i++) {  // 10 * 10 * 10 = 1000 products
//                    if (productCount >= 1000) break;
//
//                    ProductIndex.Brand brand = new ProductIndex.Brand(
//                            "brand" + (brands.indexOf(brandName) + 1),
//                            brandName
//                    );
//
//                    ProductIndex p = new ProductIndex();
//                    p.setId(UUID.randomUUID());
//                    p.setSku("sku-" + (productCount + 1));
//                    p.setImages(new ArrayList<>());
//                    p.setName(brandName + " " + type + " " + i);
//                    p.setDescription("Description for " + brandName + " " + type + " " + i);
//                    p.setRating("4.5");
//                    p.setPrice(BigDecimal.valueOf(Math.random() * 1000));
//                    p.setBrand(brand);
//                    p.setCategory(new ProductIndex.Category(
//                            "category" + (int) Math.round(Math.random() * 100),
//                            "Category " + (int) Math.round(Math.random() * 100)
//                    ));
//                    p.setQuantity((int) Math.round(Math.random() * 100));
//                    p.setSize("size" + (int) Math.round(Math.random() * 100));
//                    p.setColor("color" + (int) Math.round(Math.random() * 100));
//                    p.setModel("model" + (int) Math.round(Math.random() * 100));
//                    p.setCategory(new ProductIndex.Category(
//                            "category" + (int) Math.round(Math.random() * 100),
//                            "Category " + (int) Math.round(Math.random() * 100)
//                    ));
//                    p.setCreatedAt(Instant.now());
//                    p.setUpdatedAt(Instant.now());
//
//                    products.add(p);
//                    productCount++;
//                }
//                if (productCount >= 1000) break;
//            }
//            if (productCount >= 1000) break;
//        }
//
//        productIndexService.saveAll(products);
      //  System.out.println("Inserted " + productCount + " meaningful sample products.");
    }
}
