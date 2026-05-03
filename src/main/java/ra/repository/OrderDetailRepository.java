package ra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ra.model.entity.OrderDetail;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail,Long> {
    @Query("SELECT od.product.proName, SUM(od.quantity) " +
            "FROM OrderDetail od " +
            "GROUP BY od.product.proName " +
            "ORDER BY SUM(od.quantity) DESC")
    List<Object[]> findTopSellingProductNames();
}
