package com.giang.product_service.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.giang.product_service.models.Product;

public interface ProductRepository extends MongoRepository<Product, String> {
    Optional<Product> getProductsByName(String name);
}
