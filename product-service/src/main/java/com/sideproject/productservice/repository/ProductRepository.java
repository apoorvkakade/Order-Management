package com.sideproject.productservice.repository;

import com.sideproject.productservice.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {
    void deleteProductByNameContainsIgnoreCase(String name);
    List<Product> findProductsByNameContainsIgnoreCase(String name);
}
