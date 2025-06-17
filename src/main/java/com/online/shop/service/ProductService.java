package com.online.shop.service;

import com.online.shop.dto.ProductDto;
import com.online.shop.entity.Product;
import com.online.shop.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product createProduct(ProductDto productDto) {
        if (productRepository.existsByXAndY(productDto.getX(), productDto.getY())) {
            throw new IllegalArgumentException("A product already exists at coordinates (" + 
                productDto.getX() + ", " + productDto.getY() + ")");
        }

        Product product = new Product(
            productDto.getName(),
            productDto.getPrice(),
            productDto.getQuantity(),
            productDto.getX(),
            productDto.getY()
        );

        return productRepository.save(product);
    }

    public Product updateProduct(UUID id, ProductDto productDto) {
        Product existingProduct = productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));

        // Check if coordinates are being changed and if they conflict with another product
        if (!existingProduct.getX().equals(productDto.getX()) || !existingProduct.getY().equals(productDto.getY())) {
            Optional<Product> productAtLocation = productRepository.findByXAndY(productDto.getX(), productDto.getY());
            if (productAtLocation.isPresent() && !productAtLocation.get().getId().equals(id)) {
                throw new IllegalArgumentException("A product already exists at coordinates (" + 
                    productDto.getX() + ", " + productDto.getY() + ")");
            }
        }

        existingProduct.setName(productDto.getName());
        existingProduct.setPrice(productDto.getPrice());
        existingProduct.setQuantity(productDto.getQuantity());
        existingProduct.setX(productDto.getX());
        existingProduct.setY(productDto.getY());

        return productRepository.save(existingProduct);
    }

    public void deleteProduct(UUID id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Product> getAvailableProducts() {
        return productRepository.findAllAvailableProducts();
    }

    @Transactional(readOnly = true)
    public Optional<Product> findById(UUID id) {
        return productRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Product> findByNames(List<String> names) {
        return productRepository.findByNameIn(names);
    }

    @Transactional(readOnly = true)
    public List<Product> searchByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    public ProductDto convertToDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setQuantity(product.getQuantity());
        dto.setX(product.getX());
        dto.setY(product.getY());
        return dto;
    }
} 