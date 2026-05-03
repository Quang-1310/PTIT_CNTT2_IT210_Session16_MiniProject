package ra.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ra.model.entity.CartItem;
import ra.model.entity.Order;
import ra.model.entity.OrderDetail;
import ra.repository.OrderDetailRepository;
import ra.repository.OrderRepository;
import ra.repository.ProductRepository;
import ra.service.OrderService;
import ra.service.ProductService;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ProductService productService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addOrder(Order order , List<CartItem> cartItems) {
        Double totalAmount = cartItems.stream().mapToDouble(item -> item.getQuantity() * item.getProduct().getPrice()).sum();
        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);
        for (CartItem cartItem : cartItems) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(savedOrder);
            orderDetail.setProduct(cartItem.getProduct());
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetail.setPrice(cartItem.getProduct().getPrice());
            orderDetailRepository.save(orderDetail);
            productService.reduceStock(cartItem.getProduct().getProId(), cartItem.getQuantity());
        }
    }
}
