package com.sunit.groceryplus.repositories;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;

import com.sunit.groceryplus.models.Product;

/**
 * Stub implementation for ProductRepository in repositories package
 * This is a placeholder to resolve compilation errors
 */
public class ProductRepository {
    
    private Context context;
    private com.sunit.groceryplus.ProductRepository realRepository;
    
    public ProductRepository(Context context) {
        this.context = context;
        this.realRepository = new com.sunit.groceryplus.ProductRepository(context);
    }
    
    public List<Product> getAllProducts() {
        return realRepository.getAllProducts();
    }
    
    public Product getProductById(int id) {
        return realRepository.getProductById(id);
    }
    
    public boolean addProduct(Product product) {
        return realRepository.addProduct(
            product.getProductName(),
            product.getCategoryId(),
            product.getPrice(),
            product.getDescription(),
            product.getImage(),
            product.getStockQuantity(),
            product.getVendorId()
        );
    }
    
    public boolean updateProduct(Product product) {
        return realRepository.updateProduct(
            product.getProductId(),
            product.getProductName(),
            product.getCategoryId(),
            product.getPrice(),
            product.getDescription(),
            product.getImage(),
            product.getStockQuantity(),
            product.getVendorId()
        );
    }
    
    public boolean deleteProduct(int id) {
        return realRepository.deleteProduct(id);
    }
}
