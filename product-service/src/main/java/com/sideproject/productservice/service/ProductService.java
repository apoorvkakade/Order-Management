package com.sideproject.productservice.service;

import com.sideproject.productservice.dto.ProductRequest;
import com.sideproject.productservice.dto.ProductResponse;
import com.sideproject.productservice.model.Product;
import com.sideproject.productservice.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository)
    {
        this.productRepository = productRepository;
    }

    public void createProduct(ProductRequest productRequest)
    {
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .build();

        productRepository.save(product);
        log.info("Product {} is saved", product.getId());
    }

    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(product -> mapToProductResponse(product)).collect(Collectors.toList());
    }

    public List<ProductResponse> getProductsByName(String name)
    {
        List<Product> products = productRepository.findProductsByNameContainsIgnoreCase(name);
        return products.stream().map(product -> mapToProductResponse(product)).collect(Collectors.toList());
    }
    public void deleteProductByTitle(String name)
    {
        this.productRepository.deleteProductByNameContainsIgnoreCase(name);
    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .build();
    }



}
