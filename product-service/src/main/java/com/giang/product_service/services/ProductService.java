package com.giang.product_service.services;

import com.giang.product_service.dto.ProductRequest;
import com.giang.product_service.dto.ProductResponse;
import com.giang.product_service.models.Product;
import com.giang.product_service.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;

    public List<ProductResponse> getAllProducts() {
        List<Product> productList = productRepository.findAll();
        return productList.stream()
                          .map(this::mapToProductResponse)
                          .toList();
    }

    public Product createProduct(ProductRequest newProduct) {
        Product product = Product.builder()
                                 .name(newProduct.name())
                                 .description(newProduct.description())
                                 .price(newProduct.price())
                                 .build();
        productRepository.save(product);
        log.info("Product id: {} is saved", product.getId());
        return product;
    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                              .id(product.getId())
                              .name(product.getName())
                              .description(product.getDescription())
                              .price(product.getPrice())
                              .build();
    }
}
