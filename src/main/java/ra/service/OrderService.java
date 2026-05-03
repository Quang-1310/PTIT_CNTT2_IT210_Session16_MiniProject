package ra.service;

import ra.model.entity.CartItem;
import ra.model.entity.Order;

import java.util.List;

public interface OrderService {
    void  addOrder(Order order , List<CartItem> cartItems);
}
