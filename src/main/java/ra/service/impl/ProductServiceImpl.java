package ra.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ra.model.entity.Category;
import ra.model.entity.Product;
import ra.repository.ProductRepository;
import ra.service.ProductService;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Override
    public Product getProductById(Long proId) {
        return productRepository.findProductByProId(proId);
    }

    @Override
    public Page<Product> getAllProducts(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber-1, pageSize);
        return productRepository.findByIsDeleteFalse(pageable);
    }

    @Override
    public void addProduct(Product product) {
        productRepository.save(product);
    }

    @Override
    public void updateProduct(Product product) {
        productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long proId) {
        Product product = productRepository.findProductByProId(proId);
        product.setDelete(true);
        productRepository.save(product);
    }

    @Override
    public List<Product> getProductByCategory(Long catId) {
        return productRepository.findProductByCategoryCatId(catId);
    }

    @Override
    public Page<Product> getProductByProNameAndPriceAndCategory(String proName, Double min, Double max, Category category, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);

        return productRepository.findAll((root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("isDelete"), false));

            if (proName != null && !proName.trim().isEmpty()) {
                predicates.add(cb.like(
                        cb.lower(root.get("proName")),
                        "%" + proName.toLowerCase() + "%"
                ));
            }

            if (min != null && max != null) {
                predicates.add(cb.between(root.get("price"), min, max));
            }
            else if (min != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), min));
            }
            else if (max != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), max));
            }

            if (category != null) {
                predicates.add(cb.equal(root.get("category"), category));
            }

            return cb.and(predicates.toArray(new Predicate[0]));

        }, pageable);
    }

    @Override
    @Transactional(rollbackFor =  Exception.class)
    public void reduceStock(Long proId, Integer quantity) {
        Product product = productRepository.findProductByProId(proId);

        if (product.getQuantity() < quantity) {
            throw new RuntimeException("Sản phẩm " + product.getProName() + " chỉ còn " + product.getQuantity() + " sản phẩm trong kho.");
        }
        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);
    }

    @Override
    public List<Product> getActiveProductByCategory(Long catId) {
        return productRepository.findByCategoryCatIdAndIsDeleteFalse(catId);
    }
}
