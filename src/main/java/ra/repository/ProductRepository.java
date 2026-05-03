package ra.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ra.model.entity.Category;
import ra.model.entity.Product;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> , JpaSpecificationExecutor<Product> {
    Page<Product> findByIsDeleteFalse(Pageable pageable);
    Product findProductByProId(Long proId);
    List<Product> findProductByCategoryCatId(Long catId);
    List<Product> findByCategoryCatIdAndIsDeleteFalse(Long catId);
    Page<Product> findProductByProNameContainingIgnoreCaseAndPriceBetweenAndCategory(String proName, Double minPrice, Double maxPrice , Category category, Pageable pageable);
}
