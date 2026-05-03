package ra.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ra.model.entity.Order;
import ra.model.entity.Product;
import ra.repository.OrderDetailRepository;
import ra.repository.OrderRepository;

import java.util.List;

@Controller
@RequestMapping("/statistics")
public class StatisticsController {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @GetMapping("/detail")
    public String detail(Model model) {
        List<Order> orders = orderRepository.findAll();
        Double totalProfit =  0.0;
        for(Order order : orders){
            totalProfit += order.getTotalAmount();
        }
        List<Object[]> top5Data = orderDetailRepository.findTopSellingProductNames();
        List<Object[]> top5 = top5Data.stream().limit(5).toList();
        model.addAttribute("products" , top5);
        model.addAttribute("totalProfit", totalProfit);
        return "admin/statistics";
    }
}
