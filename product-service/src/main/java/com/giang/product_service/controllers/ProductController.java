package com.giang.product_service.controllers;

import com.giang.product_service.dto.ProductRequest;
import com.giang.product_service.dto.ProductResponse;
import com.giang.product_service.models.Product;
import com.giang.product_service.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getAllProducts() {
        // Simulate time delay
        // try {
        //     Thread.sleep(5000);
        // } catch (Exception e) {
        //     throw new RuntimeException(e.getMessage());
        // }
        return productService.getAllProducts();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product createProduct(@RequestBody ProductRequest newProduct) {
        return productService.createProduct(newProduct);
    }
}
