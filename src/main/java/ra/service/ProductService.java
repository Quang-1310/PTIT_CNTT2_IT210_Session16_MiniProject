package ra.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ra.model.entity.Category;
import ra.model.entity.Product;

import java.util.List;

public interface ProductService {
    Product getProductById(Long proId);
    Page<Product> getAllProducts(Integer pageNumber, Integer pageSize);
    void addProduct(Product product);
    void updateProduct(Product product);
    void deleteProduct(Long proId);
    List<Product> getProductByCategory(Long proId);
    Page<Product> getProductByProNameAndPriceAndCategory(String proName, Double min , Double max, Category category, Integer page , Integer pageSize);
    void reduceStock(Long proId , Integer quantity);
    List<Product> getActiveProductByCategory(Long catId);
}
