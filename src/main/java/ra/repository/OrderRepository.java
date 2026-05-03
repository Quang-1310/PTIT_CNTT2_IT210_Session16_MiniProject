package ra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import ra.model.entity.Order;

@RequestMapping
public interface OrderRepository extends JpaRepository<Order, Long> {
}
